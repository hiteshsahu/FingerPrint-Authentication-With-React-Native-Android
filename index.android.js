import Route from './src/navigation/Route'
import React, { Component } from 'react'
import {
   AppRegistry,
   View
} from 'react-native'


class AProject extends Component {
   render() {
      return (
         <Route />
      )
   }
}

AppRegistry.registerComponent('AProject', () => AProject)
