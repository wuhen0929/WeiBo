'use strict';

import React from 'react';
var ReactNative = require('react-native');
var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  ToastAndroid
} = ReactNative;

class MyAwesomeApp extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.hello}>Hello, World</Text>
        <TouchableHighlight onPress={() => ToastAndroid.show("this is a toast", ToastAndroid.SHORT)}>
          <Text>Proper Touch Handling</Text>
        </TouchableHighlight>
      </View>
    )
  }
}
var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  hello: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});

AppRegistry.registerComponent('MyAwesomeApp', () => MyAwesomeApp);
