import Router from './src/navigation/Route'
import React, { Component } from 'react'
import {
   AppRegistry,
   View
} from 'react-native'


class reactTutorialApp extends Component {
   render() {
      return (
         <Route />
      )
   }
}

AppRegistry.registerComponent('ReactMovies', () => ReactMovies)
