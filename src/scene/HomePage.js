import Style from '../Style';
import React, { Component } from 'react';
import { Platform } from 'react-native';
import FingerPrintAndroid from '../BioMetric';
import {
  AppRegistry,
  StyleSheet,
  TextInput,Button,ToastAndroid,
  Image, Text, Switch,
  DrawerLayoutAndroid,
  ToolbarAndroid,
  TouchableOpacity,
  View
} from 'react-native';

export default class HomePage extends Component {

  renderNavigationView() {
    return <View>
      <Text>Home</Text>
    </View>
  }

  constructor(props) {
   super(props);
   this.state = { text: 'Home Page' ,
 };
   this.openDrawer = this.openDrawer.bind(this);
   this.goToAbout = this.goToAbout.bind(this);
   this.goToLogin = this.goToLogin.bind(this);
 }

 openDrawer() {
    this.drawer.openDrawer();
}

  onActionSelected(position) {
  }

  goToAbout() {
        this.props.navigator.push({
        name: 'About',
        title: 'About',
     });
   }

   goToLogin() {
           this.props.navigator.push({
           name: 'Login',
           title: 'Login',
        });
   }

   state = {
      switchValue: false
   }

 toggleSwitch = (value) => {
   this.setState({ switchValue: value })
   FingerPrintAndroid.storeUserSettings('LOCK_FINGERPRINT',value)
 }


 componentDidMount = () => {

   if(Platform.OS === 'android')
   {
     FingerPrintAndroid.retrieveUserSettings('LOCK_FINGERPRINT',
     (enableFingerPrint) => {
    // ToastAndroid.show('enableFingerPrint : '+ enableFingerPrint,ToastAndroid.LONG);
     this.setState({ switchValue: enableFingerPrint })
   })
 }
 }

  render() {

  var navigationView = (
     <View style={{flex: 1, backgroundColor: '#292f36'}}>

       <Image style ={Style.thumb} source = {require('../assets/img/header.jpeg')} />
       <Text  style = {Style.button} >Home</Text>

       <TouchableOpacity
       onPress = {this.goToAbout}>
       <Text style =  {Style.button}>About </Text >
    </TouchableOpacity>

     </View>
   );

    return (
  <DrawerLayoutAndroid
        drawerWidth={300}
        ref={(_drawer) => this.drawer = _drawer}
        drawerPosition={DrawerLayoutAndroid.positions.Left}
        renderNavigationView={() => navigationView}>

        <View style={Style.container}>

          <ToolbarAndroid
            style={Style.toolbar}
            title="Home Page"
            titleColor =  "#5cc8ff"
            onIconClicked={this.openDrawer}
            navIcon={require("../assets/img/menu.png")}
            onActionSelected={this.onActionSelected}
            actions = {[
              {title: "Log out",  show: "always", iconName: 'person', iconColor:'#fff',iconSize:24 , titleColor:"#FFF"}
            ]}
            />
                <Text style={Style.welcome}>
                 Welcome to React Native App!
               </Text>

                 <Text style={Style.instructions}>
                 Start Typing to Search Contents
                  </Text>


                  <Button
                     title="Log Out"
                     onPress={this.goToLogin}
                   />

                   <View >

                   <Text style={Style.label}>
                    Unlock Finger Print Authentication!
                  </Text>

                   <Switch onValueChange = {this.toggleSwitch}
                    value = {this.state.switchValue}
                    />
                  <Text >{this.state.switchValue ? 'ON' : 'OFF'}</Text>

                   </View>
                  <View style= {Style.container}>
                  </View>


    </View>
  </DrawerLayoutAndroid>

    );
  }
}

AppRegistry.registerComponent('HomePage', () => HomePage);
