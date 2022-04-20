from rest_framework import viewsets, status, serializers
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import (
    IsAuthenticated,
    IsAuthenticatedOrReadOnly,
    SAFE_METHODS,
)
from django.db.models import Sum
from game_of_thrones.models import Event, User

class AnonNameField(serializers.Field):
    def to_representation(self, value):
        return f'{value.first_name} {value.last_name}'

class CustomUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [
            "name"
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
        ]


class EventViewSet(viewsets.ModelViewSet):
    """
    Viewset for handling tutoring requests.
    """
    queryset = Event.objects.all()
    permission_classes = [IsAuthenticatedOrReadOnly]
    ordering_fields = ["created_time"]
    serializer_class=EventSerializer

    def get_serializer_class(self):
        if self.request.method in SAFE_METHODS:
            return EventSerializerRead
        else:
            return EventSerializer

    @action(detail=False)
    def leaderboard(self, request):
        events = Event.objects.values('score').order_by('user').annotate(overall_score=Sum('score')).aggregate()
        page = self.paginate_queryset(events)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(events, many=True)
        return Response(serializer.data)

