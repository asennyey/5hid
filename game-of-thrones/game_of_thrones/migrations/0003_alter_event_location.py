# Generated by Django 3.2.12 on 2022-04-20 23:25

import django.contrib.gis.db.models.fields
from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('game_of_thrones', '0002_auto_20220420_1822'),
    ]

    operations = [
        migrations.AlterField(
            model_name='event',
            name='location',
            field=django.contrib.gis.db.models.fields.PointField(srid=4326),
        ),
    ]