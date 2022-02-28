FROM openjdk:11
EXPOSE 9090:9090
RUN mkdir /app
COPY ./build/install/calendar-poll/ /app/
WORKDIR /app/bin
CMD ["./calendar-poll"]