@anne
Feature: Register New Sampling-Site

    As an administrator of the OSD-Registry,
    I want to input participant data in the registry,
    so that I can store and manage these data.
    
Scenario: The administrator wants to input participant data

    Given the admin is on input participant data page "http://www.megx.net/bla/bla"   
    When he submits data by clicking the save button
    Then ensure the data were stored by displaying the message "Saving successful."       