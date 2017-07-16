# FingerPrint-Authentication-With-React-Native-Android

  This React Native project uses of Finger Print Sensor to authenticated user in Android Devices. 
  
## Features
-  Native Module to use fingerprint Sensor 
-  Error Handling in the case of invalid Finger Prints, Unregistered FingerPrint, Lock is not enabled and not supporting Hardware.
-  After exceeding max finger print attempts, the plugin disables biometric authentication feature.
-  Disables biometric authentication can be enabled from Home Screen after successful login
-  User Credentials are Encrypted with AES algorithm and stored in file system while key is stored in Android Keystore
-  Check for Rooted device and flush stored credentials 

## Screen Shots

![Alt text](https://github.com/hiteshsahu/Android-Audio-Recorder-Visualization-Master/blob/master/Art/recording_play.png "Playback screen")


## UseFul Commands 

#### Install react native
 
   npm install -g react-native-cli

####  Start server

   react-native start

#### For Route component install react-native-deprecated-custom-components

    npm install react-native-deprecated-custom-components --save

#### Build and install App on Emulator/Device

     react-native run-android

#### To run app in real device

    adb reverse tcp:8081 tcp:8081
    
 ## Legal Notice    

    /*
     *
     * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
     * Unauthorized copying of this file, via any medium is strictly prohibited
     * Proprietary and confidential.
     * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
     *
     */



