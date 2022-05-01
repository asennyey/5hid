from rest_framework import viewsets, status, serializers
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import (
    IsAuthenticated,
    IsAuthenticatedOrReadOnly,
    SAFE_METHODS,
)
from django.db.models import Sum
from django.db.models.functions import Coalesce
from django_filters import FilterSet, CharFilter, Filter
from django_filters.constants import EMPTY_VALUES
from django_filters import rest_framework as filters
import timeit

from game_of_thrones.models import Event, User
from django.contrib.gis.geos import GEOSGeometry

def get_location_filter_params(query, query_object):
    if query is not None:
        query_input = {}
        if 'latitude' in query and 'longitude' in query:
            latitude = float(query['latitude'])
            longitude = float(query['longitude'])
            if 'distance' in query:
                pnt = GEOSGeometry(f'POINT({longitude} {latitude})')
                query_input[f'{query_object}__distance_lte'] = (pnt, float(query['distance']))
    return query_input


def filter_location(query_set, query, query_object):
    return query_set.filter(
        **get_location_filter_params(query, query_object)
    )


class LocationFilterBackend(filters.DjangoFilterBackend):
    def get_filterset_kwargs(self, request, queryset, view):
        kwargs = super().get_filterset_kwargs(request, queryset, view)

        # merge filterset kwargs provided by view class
        if hasattr(view, 'get_filterset_kwargs'):
            kwargs.update(view.get_filterset_kwargs())

        return kwargs

class LocationFilterSet(FilterSet):
    # This is a gross hack, but the filter only allows a single field.
    latitude = CharFilter(method='is_close_to')
    longitude = CharFilter(method='is_close_to')
    distance = CharFilter(method='is_close_to')

    class Meta:
        model = Event
        fields = []

    def __init__(self, *args, query_object=None, **kwargs):
        super().__init__(*args, **kwargs)
        self.query_object = query_object

    def is_close_to(self, queryset, name, value):
        try:
            queryset = filter_location(queryset, self.data, self.query_object)
        except Exception as e:
            print(e)
        finally:
            return queryset

def is_friend(user, other_user):
    if not user or not user.friends:
        return False
    return other_user in user.friends.all()

class NameField(serializers.Field):
    def to_representation(self, value):
        return f'{value.first_name} {value.last_name[0]}.'

class AnonNameField(serializers.Field):
    def to_representation(self, value):
        if value.is_anon and not is_friend(self.context['request'].user, value):
            return 'Anonymous'
        return f'{value.first_name} {value.last_name[0]}.'

class CustomUserSerializer(serializers.ModelSerializer):
    name = AnonNameField(source="*")
    is_friend = serializers.SerializerMethodField('get_is_friend')

    def get_is_friend(self, obj):
        user = self.context['request'].user
        return is_friend(self.context['request'].user, obj)
    
    class Meta:
        model = User
        fields = [
            "name",
            'is_friend'
        ]

class EventSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(read_only=True, default=serializers.CurrentUserDefault())
    class Meta:
        model = Event
        fields = [
            "description",
            "user",
            "location"
        ]
    
    def save(self, **kwargs):
        """Include default for read_only `user` field"""
        kwargs["user"] = self.fields["user"].get_default()
        kwargs["score"] = 1#get_score(self.fields["location"])
        return super().save(**kwargs)

class EventSerializerRead(serializers.ModelSerializer):
    user = CustomUserSerializer()
    class Meta:
        model = Event
        fields = [
            "id",
            "user",
            "created_time",
            "location",
            "description",
            "score"
        ]


class EventViewSet(viewsets.ModelViewSet):
    """
    Viewset for handling tutoring requests.
    """
    queryset = Event.objects.select_related('user').prefetch_related('user__friends').all()
    permission_classes = [IsAuthenticatedOrReadOnly]
    ordering_fields = ["created_time"]
    filterset_class = LocationFilterSet
    filter_backends = [LocationFilterBackend]

    def get_serializer_class(self):
        if self.request.method in SAFE_METHODS:
            return EventSerializerRead
        else:
            return EventSerializer

    def get_filterset_kwargs(self):
        return {
            'query_object':'location'
        }

class LeaderboardSerializer(serializers.ModelSerializer):
    overall_score = serializers.IntegerField()
    name = AnonNameField(source="*")
    class Meta:
        model = User
        fields = ["name", 'overall_score']

class LeaderboardViewSet(viewsets.GenericViewSet):
    queryset = User.objects.prefetch_related('friends').all()
    permission_classes = [IsAuthenticatedOrReadOnly]
    ordering_fields = ["created_time"]
    serializer_class=LeaderboardSerializer

    def list(self, request):
        queryset = self.get_queryset()
        users = queryset.filter(**get_location_filter_params(self.request.GET.dict(), 'event__location'))\
            .annotate(overall_score=Coalesce(Sum('event__score'), 0)).filter(overall_score__gt=0)\
                .order_by('-overall_score')
        page = self.paginate_queryset(users)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(users, many=True)
        return Response(serializer.data)

class UserListSerializer(serializers.ModelSerializer):
    name = NameField(source="*")
    class Meta:
        model = User
        fields = ["name", 'email', 'friends', 'id']

class FriendSerializer(serializers.ModelSerializer):
    is_friend = serializers.SerializerMethodField('get_is_friend')
    name = NameField(source="*")

    def get_is_friend(self, obj):
        user = self.context['request'].user
        return is_friend(self.context['request'].user, obj)

    class Meta:
        model = User
        fields = ["name", 'email', 'id', 'is_friend']

class FriendsViewSet(viewsets.ReadOnlyModelViewSet):
    permission_classes = [IsAuthenticated]
    ordering_fields = ["created_time"]
    serializer_class=FriendSerializer

    def get_queryset(self):
        return User.objects.prefetch_related('friends').exclude(id=self.request.user.id)

    @action(detail=True, methods=["POST"])
    def add(self, request, pk=None):
        user = request.user
        try:
            user.friends.add(self.get_object())
            user.save()
            return Response(status=200)
        except:
            return Response(status=400)

    @action(detail=True, methods=["POST"])
    def remove(self, request, pk=None):
        user = request.user
        try:
            user.friends.remove(self.get_object())
            user.save()
            return Response(status=200)
        except:
            return Response(status=400)

