
import Style from '../Style';
import React, { Component } from 'react';

import {
  AppRegistry,
  StyleSheet,
  Text,TextInput,Image,
  Switch,
  Picker,
  DrawerLayoutAndroid,
  ToolbarAndroid,
  Navigator,
  TouchableOpacity,
  View,Linking ,
  Slider,
  ScrollView
} from 'react-native';


const Item = Picker.Item;

export default class AboutPage extends Component {

  renderNavigationView() {
    return <View>
      <Text>Home</Text>
    </View>
  }

  static defaultProps = {
   value: 0,
 };

  state = {
    enableLogReporting: true,
    selectedDropDown: 'key1',
    selectedPromp: 'key1',
    mode: Picker.MODE_DIALOG,
    value: this.props.value,

};

  constructor(props) {
   super(props);
   this.state = { text: 'Search Movies' };
    this.openDrawer = this.openDrawer.bind(this);
    this.goToHome = this.goToHome.bind(this);
    this.goToProfile = this.goToProfile.bind(this);
 }

   openDrawer() {
      this.drawer.openDrawer();
  }

  onActionSelected(position) {
  }

  goToHome() {
     this.props.navigator.push({
        name: 'Home',
        title: 'Home',
        openMenu: this.openMenu
     });
  }

  goToProfile() {
         this.props.navigator.push({
         name: 'Profile',
         title: 'Profile',
   });
 }

  render() {

      var navigationView = (
         <View style={{flex: 1, backgroundColor: '#292f36'}}>

         <Image style ={Style.thumb} source = {require('../assets/img/header.jpeg')} />

         <TouchableOpacity
           onPress = {this.goToHome}>
           <Text  style = {Style.button}>
            Home
           </Text>
           </TouchableOpacity>

           <Text  style = {Style.button} >About</Text>
         </View>
       );

    return (

  <DrawerLayoutAndroid
    drawerWidth={300}
    ref={(_drawer) => this.drawer = _drawer}
    drawerPosition={DrawerLayoutAndroid.positions.Left}
    renderNavigationView={() => navigationView}>

      <View style={Style.container}>
      <ScrollView>

          <ToolbarAndroid
            style={Style.toolbar}
            title="About"
            titleColor =  "#5cc8ff"
            onIconClicked={this.openDrawer}
            navIcon={require("../assets/img/menu.png")}
            onActionSelected={this.onActionSelected}
            actions = {[
              {title: "Log out",  show: "always", iconName: 'person', iconColor:'#fff',iconSize:24 , titleColor:"#FFF"}
            ]}
            />

                   <View style={Style.settings}>

                   <Text style={Style.welcome}>
                    Settings
                   </Text>

                   <Text style={Style.setting_title}>
                    App Version
                   </Text>
                  <Text style={Style.settings_value}>
                    1.0.0
                  </Text>
                  <View style={Style.divider}/>


                  <Text style={Style.setting_title}> Crash Log Reporting </Text>
                  <Switch
                  onValueChange={(value) => this.setState({enableLogReporting: value})}
                  style={{marginBottom: 10}}
                  value={this.state.enableLogReporting} />
                 <Text style={Style.settings_value}>{this.state.enableLogReporting ? 'Enable' : 'Disable'}</Text>
                  <View style={Style.divider}/>

                 <Text style={Style.setting_title}>Select Language</Text>
                  <Picker
                        style={Style.picker}
                        selectedValue={this.state.selectedDropDown}
                        onValueChange={this.onValueChange.bind(this, 'selectedDropDown')}
                        mode="dropdown">
                        <Item style = {Style.settings_value} label="English" value="key0" />
                        <Item style = {Style.settings_value} label="Hindi" value="key1" />
                  </Picker>
                  <View style={Style.divider}/>

                 <Text style={Style.setting_title}>Select TimeZOne</Text>
                 <Picker
                   style={Style.picker}
                   selectedValue={this.state.selectedPromp}
                   onValueChange={this.onValueChange.bind(this, 'selectedPromp')}
                   prompt="Select Time Zone">
                   <Item style = {Style.settings_value}  label="GMT+5:30 India Kolkata" value="key0" />
                   <Item style = {Style.settings_value}  label="UTC-08:00 USA california" value="key1" />
                 </Picker>
                <View style={Style.divider}/>

                <Text style={Style.setting_title}>Select Volume Level</Text>
                <Slider
                 style = {Style.slider}
                 value={30}
                 step={1}
                 minimumValue={0}
                 maximumValue={100}
                 minimumTrackTintColor={'green'}
                 maximumTrackTintColor={'red'}
                 onValueChange={(value) => this.setState({value: value})} />


                 <Text style={Style.settings_value} >
                 Level is   {this.state.value }
                 </Text>
                 <View style={Style.divider}/>


                 <Text style={Style.settings_value}
                 onPress={() => Linking.openURL('http://hiteshsahu.com')}>
                 © 2017 Hitesh Sahu- All Rights Reserved
                </Text>
              </View>


    </ScrollView>
    </View>
  </DrawerLayoutAndroid>

    );
  }

  onValueChange = (key: string, value: string) => {
    const newState = {};
    newState[key] = value;
    this.setState(newState);
  };
}

AppRegistry.registerComponent('AboutPage', () => AboutPage);
