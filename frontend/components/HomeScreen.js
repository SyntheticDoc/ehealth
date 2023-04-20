import { View, Text, Switch, StyleSheet, TextInput, TouchableOpacity, Alert } from 'react-native';
import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faHeartPulse } from '@fortawesome/free-solid-svg-icons/faHeartPulse';
import { faGear } from '@fortawesome/free-solid-svg-icons/faGear';
import { faUser } from '@fortawesome/free-solid-svg-icons/faUser';

const HomeScreen = ({ navigation }) => {
	const [data, setData] = useState('');
	const [healthStatus, setHealthStatus] = useState(2);

	useEffect(() => {
		clearInterval(x);
		getData();
		var x = setInterval(() => {
			getData();
		}, 10000);
	}, []);

	useEffect(() => {
		if (healthStatus == 0) {
			Alert.alert('Making Phone API Call now, EMERGENCY');
		}
		if (healthStatus == 1) {
			Alert.alert('Starting Alarm and Waiting for user input!');
		}
	}, [healthStatus]);

	const getData = () => {
		fetch('http://catfact.ninja/fact', { method: 'GET' })
			.then((response) => response.json())
			.then((responseJson) => {
				setData(JSON.stringify(responseJson));
				setHealthStatus(JSON.stringify(responseJson));
			})
			.catch((error) => {
				//Error
				// Alert.alert(JSON.stringify(error));
				console.error(error);
			});
	};
	return (
		<View style={styles.container}>
			<View style={styles.headerContainer}>
				<Text style={styles.heading}>monitor </Text>
				<FontAwesomeIcon
					style={{
						color: '#fff',
					}}
					icon={faHeartPulse}
					size={50}
				/>
			</View>
			<View
				style={[
					styles.statusCircle,
					{
						backgroundColor:
							healthStatus == 2
								? '#419a49'
								: healthStatus == 1
								? '#ffb347'
								: '#d01818',
					},
				]}
			>
				<Text
					style={{
						fontSize: 30,
						color: '#fff',
					}}
				>
					{healthStatus == 2 ? 'OK' : healthStatus == 1 ? 'WARN' : 'SOS'}
				</Text>
			</View>
			<View style={styles.apiResponse}>
				<Text style={{ color: 'white', fontSize: 20 }}>{data}</Text>
			</View>


			<TouchableOpacity
				style={styles.gear}
				onPress={() => {
					navigation.navigate('Settings');
				}}
			>
				<FontAwesomeIcon
					icon={faGear}
					style={{ color: '#454545' }}
					size={50}
				></FontAwesomeIcon>
			</TouchableOpacity>
			
			
			<TouchableOpacity
				style={styles.user}
				onPress={() => {
					navigation.navigate('Register');
				}}
			>
				<FontAwesomeIcon
					icon={faUser}
					style={{ color: '#454545' }}
					size={50}
				></FontAwesomeIcon>
			</TouchableOpacity>
			
		</View>
	);
};

export default HomeScreen;

const styles = StyleSheet.create({
	container: {
		flex: 1,
		flexDirection: 'column',
		backgroundColor: '#93CAED',
		color: 'white',
		alignItems: 'center',
		justifyContent: 'flex-start',
	},
	heading: {
		color: 'white',
		fontSize: 50,
	},
	headerContainer: {
		marginTop: 100,
		flexDirection: 'row',
		alignItems: 'center',
	},
	gear: {
		position: 'absolute',
		bottom: 0,
		right: 0,
		margin: 60,
	},
	user: {
		position: 'absolute',
		bottom: 0,
		left: 0,
		margin: 60,
	},
	apiResponse: {
		marginHorizontal: 60,
	},
	statusCircle: {
		marginVertical: 60,
		alignItems: 'center',
		justifyContent: 'center',
		height: 250,
		width: 250,
		borderRadius: 175,
	},
});
