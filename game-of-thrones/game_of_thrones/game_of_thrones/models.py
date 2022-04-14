from django.db.models import CharField, EmailField, BooleanField, SET_NULL, DateTimeField, ForeignKey, Model
from django.contrib.auth.base_user import AbstractBaseUser
from django.forms import ImageField


class User(AbstractBaseUser):
    name: CharField = CharField(max_length=200)
    email: EmailField = EmailField(unique=True)
    is_admin: BooleanField = BooleanField(default=False)
    is_active: BooleanField = BooleanField(default=False)

    USERNAME_FIELD = "email"
    EMAIL_FIELD = "email"
    REQUIRED_FIELDS = ["name", "email"]

    @property
    def is_staff(self):
        "Is the user a member of staff?"
        # Simplest possible answer: All admins are staff
        return True

    @property
    def is_superuser(self):
        "Is the user a member of staff?"
        # Simplest possible answer: All admins are staff
        return self.is_admin

    def has_perm(self, perm, obj=None):
        return True

    def has_module_perms(self, app_label):
        return True

class BaseModel(Model):
    # Default fields that are good to have.
    created_time: DateTimeField = DateTimeField(auto_now_add=True)
    modified_time: DateTimeField = DateTimeField(auto_now=True)

    class Meta:
        ordering = ["-created_time"]
        abstract = True


class Event(BaseModel):
    location: CharField = CharField(max_length=2000)
    user: ForeignKey = ForeignKey(User, on_delete=SET_NULL)
    image: ImageField = ImageField()
    description: CharField = CharField(max_length=2000)
