
import Style from '../Style';
import React, { Component } from 'react';
import { Platform } from 'react-native';
import FingerPrintAndroid from '../BioMetric';
import {
  AppRegistry,
  StyleSheet,
  Text,TextInput,Alert,
  Navigator,Button, Image,
  TouchableOpacity,AsyncStorage,
  View,Linking ,ToastAndroid,
  ScrollView
} from 'react-native';
export default class Login extends Component {

  state = {
        username: '',
        password: '',
     };

  constructor(props) {
   super(props);
   this.goToHome = this.goToHome.bind(this);
   this.authenticateFingerPrint = this.authenticateFingerPrint.bind(this);
  // this.retrieveCredentials = this.retrieveCredentials.bind(this);

 }

   goToHome = () => {
      this.props.navigator.push({
         name: 'Home',
         title: 'Home',
      });
   }

 authenticateFingerPrint = () => {
    FingerPrintAndroid.authenticateUser(this.state.username, this.state.password, (errorMessage) => {
    console.log(errorMessage);
    ToastAndroid.show('Error: '+errorMessage,ToastAndroid.SHORT);
       },
      () => {
     // USe Biometric from next time
     FingerPrintAndroid.storeUserSettings('useBioMetric', true );

    //Screen Navigation
    this.props.navigator.push({
           name: 'Home',
           title: 'Home',
           });
      });
   }

   componentDidMount = () => {

     if(Platform.OS === 'android')
     {
         FingerPrintAndroid.retrieveCredentials(
         (errorMessage) => {
         ToastAndroid.show('Error : '+errorMessage,ToastAndroid.SHORT);},(credentials) => {
            //this.setState({username: JSON.parse(credentials).userName})
            //this.setState({password: JSON.parse(credentials).passWord})

            if(Platform.OS === 'android')
         {

         FingerPrintAndroid.retrieveUserSettings('LOCK_FINGERPRINT',
         (enableFingerPrint) => {

         //ToastAndroid.show('enableFingerPrint : '+ enableFingerPrint,ToastAndroid.LONG);

         if(enableFingerPrint===true)
           {
             FingerPrintAndroid.retrieveUserSettings('useBioMetric',(useBioMetric) => {
               //  ToastAndroid.show('useBioMetric : '+ useBioMetric,ToastAndroid.LONG);
                 if(useBioMetric===true)
                   {
                     FingerPrintAndroid.authenticateUser(this.state.username, this.state.password, (errorMessage) => {
                     console.log(errorMessage);
                     ToastAndroid.show('Error: '+errorMessage,ToastAndroid.SHORT);
                        },
                       () => {
                      // USe Biometric from next time
                      FingerPrintAndroid.storeUserSettings('useBioMetric', true );

                     //Screen Navigation
                     this.props.navigator.push({
                            name: 'Home',
                            title: 'Home',
                            });
                       });

                   }
                 });
               }
             });
        }


           });
       }
     }

  render() {
    return (

    <View  style={Style.containerDetail}>

     <Image source={require('../assets/img/login.jpeg')} style={Style.imageBackground}>
      <ScrollView style={Style.plot}>

              <Text style={Style.welcome}>
                 Sign In
                </Text>

               <Text style={Style.label}>
              User Name
               </Text>
              <TextInput
                 style={Style.input}
                 placeholder="UserName"
                 onChangeText={(value) => this.setState({username: value})}
                 value={this.state.username}
               />

               <Text style={Style.label}>
              Password
               </Text>
              <TextInput
                 style={Style.input}
                 placeholder="Password"
                 onChangeText={(value) => this.setState({password: value})}
                 value={this.state.password}
               />

               <View style={{marginLeft: 10 , marginRight:10, marginTop:25}} >

               <Button
                  title="LOGIN"
                  onPress={() => {

                    if(typeof this.state.username === 'undefined' || this.state.username === null || this.state.username.localeCompare('demo')!=0)
                    {
                       ToastAndroid.show('Invalid UserName',ToastAndroid.SHORT);
                       return;
                    }

                    if(typeof this.state.password === 'undefined' || this.state.password === null || this.state.password.localeCompare('demo')!=0)
                    {
                       ToastAndroid.show('Invalid Password',ToastAndroid.SHORT);
                       return;
                    }

                    if(Platform.OS === 'android')
                    {

                    FingerPrintAndroid.retrieveUserSettings('LOCK_FINGERPRINT',
                    (enableFingerPrint) => {

                    //ToastAndroid.show('enableFingerPrint : '+ enableFingerPrint,ToastAndroid.LONG);

                    if(enableFingerPrint===true)
                      {
                        FingerPrintAndroid.retrieveUserSettings('useBioMetric',(useBioMetric) => {
                          //  ToastAndroid.show('useBioMetric : '+ useBioMetric,ToastAndroid.LONG);
                            if(useBioMetric===true)
                              {
                                // FingerPrintAndroid.authenticateUser(this.state.username, this.state.password, (errorMessage) => {
                                // console.log(errorMessage);
                                // ToastAndroid.show('Error: '+errorMessage,ToastAndroid.SHORT);
                                //    },
                                //   () => {
                                //  // USe Biometric from next time
                                //  FingerPrintAndroid.storeUserSettings('useBioMetric', true );

                                //Screen Navigation
                                this.props.navigator.push({
                                       name: 'Home',
                                       title: 'Home',
                                       });
                                  // });

                              }else {
                                Alert.alert(
                                   'FingerPrint Authentication',
                                   'Would you like to authenticate using fingerprint ?',
                                   [
                                     {text: 'Cancel', onPress: this.goToHome},
                                     {text: 'Yes', onPress: this.authenticateFingerPrint},
                                   ],
                                   { cancelable: false }
                                 )
                            }
                        });
                      } else {
                        //Screen Navigation
                        this.props.navigator.push({
                               name: 'Home',
                               title: 'Home',
                               });

                      }
                  })
                    }else {
                      //Screen Navigation
                      this.props.navigator.push({
                             name: 'Home',
                             title: 'Home',
                             });
                    }
                  }
                }
                />
               </View>

    </ScrollView>

   </Image>
    </View>
    );
  }


}

AppRegistry.registerComponent('Login', () => Login);
