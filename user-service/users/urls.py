from django.urls import path
from .views import RegisterAdminView, RegisterMemberView, LoginView, UserListView

urlpatterns = [
    path('register/admin/', RegisterAdminView.as_view(), name='register-admin'),
    path('register/member/', RegisterMemberView.as_view(), name='register-member'),
    path('login/', LoginView.as_view(), name='login'),
    path('users/', UserListView.as_view(), name='user-list'),
]