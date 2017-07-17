/**
 * Style.js
 *
 * Created by kylewbanks on 2016-08-07.
 */
'use strict';

import { StyleSheet ,Dimensions} from 'react-native';
const { width, height } = Dimensions.get('window');
const gutter = 3; // You can add gutter if you want

var Style = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',

  },
  button: {
     borderWidth: 1,
     padding: 10,
     borderColor: 'black',
     color: '#5cc8ff',
     fontSize: 18,
     fontFamily: 'Roboto-Medium',
     backgroundColor: '#292f36'
  },
  input:
  {
      height: 40,
      width: width-20,
      alignSelf: 'stretch',
      color: '#292f36',
      borderWidth: 1,
      marginLeft: 10,
      marginRight:10,
      backgroundColor: 'white'
  },

  label: {
    margin: 10,
    color: '#00c853',
    fontFamily: 'Roboto-Medium',
    fontSize: 15,
  },
  buttonClose: {
    backgroundColor: '#617D8A',         // Color the button
    padding: 15,
    margin:10                       // Padding inside
  },
  welcome: {
    textAlign: 'center',
    color: '#5cc8ff',
    fontWeight: 'bold',
    fontFamily: 'Roboto-Bold',
    fontSize: 25,
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#FFF',
    marginBottom: 5,
  },
 toolbar: {
   backgroundColor: '#131313',
   height: 56,
   alignSelf: 'stretch',
 },
 thumb:{
  width: 300,
  height: 200,
  left: 0,
  right: 0,
  top:0
},
 settings: {
    flex: 1,
    width:width,
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    padding:10,
    backgroundColor: '#292f36',
  },
    setting_title: {
      fontSize: 17,
      textAlign: 'center',
      color: '#FFF',
      margin: 5,
    },
    settings_value: {
      fontSize: 15,
      textAlign: 'center',
      color: '#FFF',
      margin: 5,
    },
    divider: {
      width: width ,
      height:2,
      margin: 10,
      backgroundColor: '#131313',
    },
    picker: {
    color: '#FFF',
    width: width,
    margin: 10,
  },
  slider: {
  width: width,
  margin: 10,
},
// Main container
containerDetail: {
 flex: 1,                            // Take up all screen space
    backgroundColor: '#333',         // Dark background
},
// Background image
imageBackground: {
 flex: 1,
 width:width,
 height:height,                      // Take up all screen space
 padding: 20                         // Add padding for content inside
},

 title: {
   color: '#5cc8ff',
   fontWeight: 'bold',
   fontFamily: 'Roboto-Bold',
   fontSize: 25,
 },
 year: {
   color: '#d63c6b',
   fontFamily: 'Roboto-Medium',
   fontSize: 20,
 },


 content: {
   color: '#FFF',
   fontFamily: 'Roboto-Medium',
   fontSize: 17,
 },

item: {
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: '#d6d7da',
    backgroundColor: '#757575',
    width: (width - gutter * 3)/(2),
    marginBottom: gutter,
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'flex-start',
  },
 buttonContainer: {
  marginTop: 20,                      // Add some margin at the top
},

buttonText: {
  color: '#fff',                      // White button text
  fontFamily: 'Avenir',               // Change default font
  fontWeight: 'bold',                 // Bold font
  textAlign: 'center',                // Center horizontally
},
plot: {
  backgroundColor: 'rgba(0,0,0,0.5)', // Semi-transparent white background
  borderRadius: 10,                   // Rounder corners
  margin: 15,                      // Margin at the top
  padding: 10,                        // Padding for content inside
},
splashBackground: {
 flex: 1,
 width:width,
 height:height,                      // Take up all screen space
 padding: 20,
 justifyContent: 'center',           // Center vertically
 alignItems: 'center',                       // Add padding for content inside
},
// Shared text style
splashText: {
  color: '#fff',                      // White text color
  backgroundColor: 'transparent',     // No background
  fontFamily: 'Avenir',               // Change default font
  fontWeight: 'bold',                 // Bold font
  // Add text shadow
  textShadowColor: '#222',
  textShadowOffset: { width: 1, height: 1 },
  textShadowRadius: 4,
    fontSize: 35,
},
});

export default Style;
