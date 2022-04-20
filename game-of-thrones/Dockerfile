###########
# BUILDER #
###########

# pull official base image
FROM python:3.9-slim-buster as builder

# set work directory
WORKDIR /usr/src/app

# set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Update the package listing, so we know what package exist:
RUN apt-get update

# Install security updates:
RUN apt-get -y upgrade
RUN apt-get -y install --no-install-recommends make automake gcc g++ subversion

# install psycopg2 dependencies
RUN apt-get -y install libpq-dev

# install dependencies
COPY ./requirements.txt .
RUN pip wheel --no-cache-dir --no-deps --wheel-dir /usr/src/app/wheels -r requirements.txt


#########
# FINAL #
#########

# pull official base image
FROM python:3.9-slim-buster

ENV USER=app
ENV GROUP=app
ENV ID=11111

# create directory for the app user
RUN mkdir -p /home/app

# create the app user
RUN groupadd -g ${ID} ${GROUP} && useradd -u ${ID} -g ${GROUP} -s /bin/sh ${USER}

# create the appropriate directories
ENV HOME=/home/app
ENV APP_HOME=/home/app/web
ENV LOG_DIR=/var/log


RUN mkdir $APP_HOME
RUN mkdir $APP_HOME/staticfiles
WORKDIR $APP_HOME

# Update the package listing, so we know what package exist:
RUN apt-get update

# Install security updates:
RUN apt-get -y upgrade
RUN apt-get -y install --no-install-recommends make automake gcc g++ subversion
# Setup GDAL
RUN apt-get install -y binutils libproj-dev gdal-bin python-gdal python3-gdal

COPY --from=builder /usr/src/app/wheels /wheels
COPY --from=builder /usr/src/app/requirements.txt .
RUN pip install --no-cache /wheels/*

# copy project
COPY . $APP_HOME

# chown all the files to the app user
RUN chown -R $USER:$GROUP $APP_HOME
RUN chown -R $USER:$GROUP $LOG_DIR

# change to the app user
USER $USER

EXPOSE 8000

CMD gunicorn game_of_thrones.wsgi --bind 0.0.0.0:8000 --timeout 0 --worker-tmp-dir /dev/shm --workers=2 --threads=4
