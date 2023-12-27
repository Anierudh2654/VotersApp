#Voters App
This is an android application used for e-voting purposes. In this app users login via email and password method, with additional email verification.
Then they have an option to register as candidate, or vote and view results on specified dates as mentioned in the database Firebase.
Registered candidates can chat online as campaign and debate within each other, which is available for the public to view.
Fresh elections can simply be initialised by changing the dates in database and clearing existing fields, ensuring reusability of the app.


Android APK File is available on android-app-submission zip file

|-----------------------------------------------------------------------------------|
|The FireStore Database has the following:-                                         |
|-----------------------------------------------------------------------------------|
|Collections  |  Document       |     Fields                                        |
|Dates        | dates           |   registration_date,results_date and voting_date  |
|candidates   |  UserUID        |    documentId,name,voteCount                      |
|chat         |  auto-generated |    chat,name,timestamp                            |
|uservotes    |  email ids      |    NULL                                           |
-------------------------------------------------------------------------------------
