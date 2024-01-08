Project using JDK 17 and Maven 3.9.2 <br>
``How to run:`` <br>

    1. cd ./docker <br>
    2. docker-compose up -d mysql redis 
    3. cd ../ 
    4. mvn package 
    5. cd .docker 
    6. docker-compose up -d app 
``APIs:``

    
1. GET /api/v1/search <br>
    Request params: <br>
   {<br>
       departureAirportCode: SGN<br>
       arrivalAirportCode: HKG<br>
       departureDate: 2024-01-23<br>
 }<br>
2. GET /api/v1/poll <br>
    Request params: <br>
   {<br>
   searchId:369b91e4-7c04-4cf7-9801-babbbb8c9adb <br>
   afterSequenceNumber:1704694017495  (optional, server will only return the schedules with sequenceNumber larger than this number)<br> 
   }<br>

``Some notes:``

1. The project is running on port 8080, 6379, 3306
2. The project needs mysql and redis running to run the tests
3. For properly handling the sequenceNumber in distributed systems, we will need a Sequence Generator Service
4. Requests to The Provider1 have a delay of 2s, check stimulateSlowNetwork()
5. Requests to The Provider2 have a delay of 15s, check stimulateSlowNetwork()
6. That means that the polling API will return data from The Provider1 first, then The Provider2 data will be there after 15s.
