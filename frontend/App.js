import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import HomeScreen from './components/HomeScreen';
import SettingsScreen from './components/SettingsScreen';
import LoginScreen from './components/LoginScreen';
import RegisterScreen from './components/RegisterScreen';
import { createStackNavigator } from '@react-navigation/stack';
import React from 'react'
import Toast from 'react-native-toast-message';

import User from "./type/User"
import { useState } from 'react';



//const [user, setUser] = useState(''); 

export const AppContext = React.createContext(null);

export default function App() {

	const Stack = createStackNavigator();
	const [generaluser, setGeneraluser] = useState(null); 
	return (
		<AppContext.Provider value={{generaluser, setGeneraluser}}>
			<NavigationContainer>
			<Stack.Navigator
				screenOptions={{
					headerShown: false,
				}}
			><Stack.Screen name='Home' component={HomeScreen} />
				<Stack.Screen name='Register' component={RegisterScreen} />
				
				<Stack.Screen name='Settings' component={SettingsScreen} />
				<Stack.Screen name='Login' component={LoginScreen} />
				
			</Stack.Navigator>
		</NavigationContainer>
		<Toast/>
		</AppContext.Provider>
		
	);
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: '#fff',
		alignItems: 'center',
		justifyContent: 'center',
	},
});
