# File storage service trial work

## Prerequisites
- JDK 1.8
- Gradle 4.4

## Usage 
1. Build using `gradle build`
2. Do one of these to have a valid directory for the uploaded files
   - Create a subdirectory called `files` in the directory you will start the application
   - Configure a custom directory for the uploaded files. To do this create a file called `application.properties` in the directory where the built JAR file is present with the content `storage.filesystem.path=path_to_your_file_storage_directory`, where `path_to_your_file_storage_directory` is a directory in your file system. 
3. Start the application with `java -jar storage-service-0.1.jar`
4. To upload a file send a multipart PUT request to `http://localhost:8080/file-storage`. It will return a string like `GK/the_uploaded_filename`. This is the path where the uploaded file is accessible. The first part (GK in this example) is to avoid filename collisions. 
5. To download the uploaded file send a GET request to an URL like `http://localhost:8080/file-storage/GK/the_uploaded_filename` where the last two parts are values returned by the put request in the previous step. 
