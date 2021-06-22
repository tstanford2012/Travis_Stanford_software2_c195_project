WGU-Software 2-C195 project

The purpose of this application is to create a GUI based scheduling application for an organization with offices in several countries.

Author: Travis Stanford
Contact: tstan36@wgu.edu
Version: Version 1.17
Date: 06/20/2021

IDE version: Intellij IDEA 2021.1.2 (Ultimate Edition)
JavaFX version: JavaFX-SDK-11.0.2
Java JDK version: Java SE 11.0.1
MySQL Connector version: mysql-connector-java-8.0.22.jar

Directions: 
1) Login Page: This page simply takes a username and password to log into the application. The labels and error messages can be translated into english or french depending on the User's system language.
	1.a) For testing of the appointment alert, see sections 3 and 3.a.1.

2) Main screen: This page has 3 buttons to allow you to navigate to the 3 main parts of the application(Appointments, Customers, Reports).

3) The appointments screen has a table view that displays all of the appointments that are associated with the logged in User. All times are displayed in the User's local time. There are two radio buttons near the top that allow for filtering of appointments in the next week or month. The 3 buttons are the bottom allow the user to Add, edit, or cancel/delete appointments.

	3.a) The add appointment screen allows the user to enter information into the text fields and combo boxes to add a new appointment. To properly view times, the Location combo box needs to be selected, a start date needs to be entered, then the "check times" button needs to be clicked.

		3.a.1) For easy testing of the 15 min alert after logging in, there is a button that prepopulates most of the fields on the add appointment screen. Simply click on the top left corner of the window and the button will then be revealed in the bottom left corner. This will automatically set the start time to be the current date and in 15 min in the User's local time.

		3.a.2) There are two radio buttons at the top of the add appointment screen, but one is currently disabled. Functionality to be added at a later time.

		3.a.3) The cancel button can be used to go back to the appointments screen.

		3.a.4) The end date is automatically populated when the check times button is clicked.

	3.b) The edit appointment screen functionality is very similar to the add appointment screen with some exceptions. There is no test appointment button and the data is prepolulated in the fields from the record that was selected in the table.

	3.c) The cancel appointment button allows the user to cancel/delete an appointment from the table

4) 	The customers screen has a table view that displays all of the customers in the database. This screen has buttons that allow the User to add, edit or delete a customer. Deleting a customer also deletes all appointments associated with that customer.

	4.a) The add customer screen allows the User to enter information into the text fields and combo boxes to add a new customer to the database. The country must be selected before clicking on the state/province combo box in order to see results. If "United Kingdom" is selected as the country, an additional field called "borough" appears in order to have an accurate address. The save and cancel buttons have similar functionality to other screens.

	4.b) The edit customer screen functionality is very similar to the add screen with one exception. The fields will be prepopulated with the data from the customer that was selected on the table to be edited.

	4.c) The delete customer button allows the User to delete a customer from the database.

5) 	The reports screen has two buttons that will go to either report screen
	
	5.a) The Total Appointments screen allows the user to select a customer and either the appointment type or month to vew the amount of appointments that match the criteria. The month displays the total amount of appointments for the next month for the customer selected. The appointment type displays the total appointments for each type regardless of the timeframe for the selected customer.

	5.b) The contact schedule screen displays the appointments in a table view for the selected contact. A combo box is used to select a contact, the "view" button is pressed, and the information is displayed.

6) Note: The database will automatically disconnect after some time of inactivity from the application. This causes an "EOFException" to be thrown. I cannot change this as I do not have privilege to change the attributes of the database. If this occurs, simply close the program and reopen it to resume intended operation.



Additional Report: The additional report used for part A3f is located on the editAppointmentScreen. The report tracks each time an appointment is edited or attempted to be edited. The report saves to a file called appointment_changes.txt. The timestamp, username, appointmentID, and appointment information are saved to the text file as well as information about whether the change was successful or not.