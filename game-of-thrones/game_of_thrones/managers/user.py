from django.contrib.auth.base_user import BaseUserManager
from django.db import models


class UserManager(BaseUserManager):
    def create_user(
        self, email, first_name, last_name, is_admin=False, is_active=True, password=None
    ):
        """
        Creates and saves a User with the given information and password.
        """
        if not email:
            raise ValueError("Users must have an email address")

        user = self.model(
            email=self.normalize_email(email),
            first_name=first_name,
            last_name=last_name,
            is_active=is_active,
            is_admin=is_admin,
        )

        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, email, password=None, **kwargs):
        """
        Creates and saves a superuser with the given information and password.
        """
        user = self.create_user(
            email,
            password=password,
            is_admin=True,
            is_active=True,
            **kwargs,
        )
        user.save(using=self._db)
        return user
