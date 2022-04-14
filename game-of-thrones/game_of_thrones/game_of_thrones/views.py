from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import (
    IsAuthenticated,
    IsAuthenticatedOrReadOnly,
    SAFE_METHODS,
)
from game_of_thrones.models import Event


class TutoringRequestViewSet(viewsets.ModelViewSet):
    """
    Viewset for handling tutoring requests.
    """
    permission_classes = [IsAuthenticatedOrReadOnly]
    filterset_fields = ["status", "tutor"]
    ordering_fields = ["created_time"]

    def get_queryset(self):
        return Event.objects.filter(user=self.request.user)
