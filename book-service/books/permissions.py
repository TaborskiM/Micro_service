from rest_framework import permissions
import requests
from rest_framework_simplejwt.tokens import AccessToken

class IsAdminOrReadOnly(permissions.BasePermission):
    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            # Allow GET requests if user is authenticated
            return request.user and request.user.is_authenticated
        
        # For POST, PUT, DELETE - check if user is admin
        if request.user and request.user.is_authenticated:
            # Check user role from token or database
            return hasattr(request.user, 'role') and request.user.role == 'ADMIN'
        return False