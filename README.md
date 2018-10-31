# javers spring boot demo

1. Start the application
$ mvn spring-boot:run 

2. To show audit information [http://localhost:8081/audit/person](http://localhost:8081/audit/person)
3. Update information to see the audit
  a. Open swagger ui [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
  b. Use put method to save the first record
  c. Use put method to update the record (You will only able to see the audit info here), hit the URL in step 2
