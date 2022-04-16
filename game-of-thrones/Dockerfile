###########
# BUILDER #
###########

# pull official base image
FROM python:3.9.6-alpine as builder

# set work directory
WORKDIR /usr/src/app

# set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# install psycopg2 dependencies
RUN apk update \
    && apk add postgresql-dev gcc python3-dev musl-dev \
            libffi-dev

# install dependencies
COPY ./requirements.txt .
RUN pip wheel --no-cache-dir --no-deps --wheel-dir /usr/src/app/wheels -r requirements.txt


#########
# FINAL #
#########

# pull official base image
FROM python:3.9.6-alpine

ENV USER=app
ENV GROUP=app

# create directory for the app user
RUN mkdir -p /home/app

# create the app user
RUN addgroup -S $USER && adduser -S $USER -G $GROUP

# create the appropriate directories
ENV HOME=/home/app
ENV APP_HOME=/home/app/web
ENV LOG_DIR=/var/log


RUN mkdir $APP_HOME
RUN mkdir $APP_HOME/staticfiles
WORKDIR $APP_HOME

# install dependencies
RUN apk update && apk add libpq
COPY --from=builder /usr/src/app/wheels /wheels
COPY --from=builder /usr/src/app/requirements.txt .
RUN pip install --no-cache /wheels/*

# copy project
COPY . $APP_HOME

RUN python manage.py collectstatic --no-input

# chown all the files to the app user
RUN chown -R $USER:$GROUP $APP_HOME
RUN chown -R $USER:$GROUP $LOG_DIR

# change to the app user
USER $USER

EXPOSE 8000

ENTRYPOINT gunicorn tutor_center.wsgi --bind 0.0.0.0:8000 --worker-tmp-dir /dev/shm --workers=2 --threads=4
