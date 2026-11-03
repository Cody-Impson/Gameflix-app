# Gameflix-app
Gameflix app semester project for IST 412
## AI Usage

I had the original layout and overall structure for the GameFlix authentication project planned the way I was thinking would work best. I used AI mainly when I needed help connecting the Spring Boot project to MySQL, setting up the tests, and troubleshooting errors that occurred while building the project.

I used AI as a reference while working through the database connection and login and registration testing and JWT authentication and configuration errors. I reviewed the suggestions and made changes to fit my project.

One change I made was simplifying some of the security and authentication code because the original suggestions were more complicated than I could understand.

11/2/26
I added and pushed the docker file but was getting errors and realized I needed to change JDK in the file from 17 to 21 which is what I'm using.

Next after funning everything and committing to Git, I received an error on Git stating my Docker never fully ran. I tried changing a few things but forgot I never pushed my local SQL database to Git as I am still refining it/ I wasn't sure how to update the Docker in order to make this happen and when asking AI I got:
       
        run: mvn clean package -DskipTests

It added -DskipTests
