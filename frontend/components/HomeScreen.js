import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import React, { useEffect, useState, useRef, useContext } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faHeartPulse } from '@fortawesome/free-solid-svg-icons/faHeartPulse';
import { faGear } from '@fortawesome/free-solid-svg-icons/faGear';
import { faUser } from '@fortawesome/free-solid-svg-icons/faUser';
import { getData, castEmergencyCall, getUserbyId } from './NetworkFunctions';
import Toast from 'react-native-toast-message';
import { AppContext } from '../App';
import { Audio } from 'expo-av';

const HomeScreen = ({ navigation }) => {
	const [healthStatus, setHealthStatus] = useState(2);
	const [countdown, setCountdown] = useState(30);
	const [name, setName] = useState('');
  const[activated,setActivated]=useState(false)

	const { generaluser, setGeneraluser } = useContext(AppContext);
	const {IPAdresse, setIPAdresse} = useContext(AppContext);
	var timer = useRef();

	
  const handlePress = () => {
    setActivated(!activated);
    console.log(activated)
    
  };

  

	const getECGdata = async () => {
		const postData = {
			userName: 'test',
			password: 'test',
			deviceIdentifier: '123',
		};

    if(healthStatus ==2 && activated==true){
		const response = await fetch(
			'http://'+IPAdresse+':8080/data/lastHealthStatus',
			{
				method: 'Post',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(postData),
			}
		);
		const json = await response.json();
		if (json.error !== undefined) {
			Toast.show({
				type: 'error',
				text1: 'FAIL to fetch ECG DATA',
			});
			console.log('FAIL to fetch ECG DATA');
		} else {
      const state = json.lastAnalysisResult.ecgstate
			console.log(state);
      if(state =="OK"){
        setHealthStatus(2);
      }else if(state =="WARNING"){
        setHealthStatus(1);
      }
			Toast.show({
				type: 'success',
				text1: 'ECG Successful',
			});
		}
  }
	};

	const [sound, setSound] = React.useState();

	async function playSound() {
		console.log('Loading Sound');
		const { sound } = await Audio.Sound.createAsync(
			require('../assets/alarm.mp3')
		);
		setSound(sound);

		console.log('Playing Sound');
		await sound.playAsync();
	}

	async function stopSound() {
		console.log('Loading Sound');

		console.log('Playing Sound');
		await sound.pauseAsync();
	}

	React.useEffect(() => {
		return sound
			? () => {
					console.log('Unloading Sound');
					sound.unloadAsync();
			  }
			: undefined;
	}, [sound]);

	
 useEffect(() => {
	   clearInterval(x);
	   console.log(generaluser);
	   getECGdata();
	   var x = setInterval(() => {
	     getECGdata();
	   }, 10000);
	 }, []);

	/*useEffect(() => {
    clearInterval(x);
    getData();
    var x = setInterval(() => {
      getData();
    }, 10000);
  }, []);
  */
	useEffect(() => {
		if (generaluser) {
			setName(generaluser.name);
		}
	}, [generaluser]);

	useEffect(() => {
		if (healthStatus == 0) {
			Alert.alert('Making Phone API Call now, EMERGENCY');
			clearInterval(timer.current);
		}
		if (healthStatus == 1) {
			Alert.alert('Starting Alarm and Waiting for user input!');
			playSound();
			timer.current = setInterval(() => {
				setCountdown((previous) => {
					if (previous == 1) {
						setHealthStatus(0);
						castEmergencyCall();
						clearInterval(timer.current);
					}
					return previous - 1;
				});
			}, 1000);
		}
		if (healthStatus == 2) {
			clearInterval(timer.current);
		}
	}, [healthStatus]);

	return (
		<View style={styles.container}>
			<View style={styles.headerContainer}>
				<FontAwesomeIcon
					style={{
						color: '#fff',
					}}
					icon={faHeartPulse}
					size={50}
				/>

				<Text style={styles.heading}>Guard</Text>
			</View>
			{generaluser !== null ? (
				<View>
					<Text style={styles.heading}>{generaluser.name} </Text>
				</View>
			) : (
				<View></View>
			)}

      <View><TouchableOpacity onPress={handlePress}>
        <Text>{activated ? 'Deaktiviert' : 'Aktiviert'}</Text>
      </TouchableOpacity></View>

			<TouchableOpacity
				onPress={() => {
					if (healthStatus == 2) {
						setHealthStatus(1);
					} else {
						clearInterval(timer.current);
						setHealthStatus(2);
						setCountdown(30);
						stopSound();
					}
				}}
			>
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
			</TouchableOpacity>

			<View style={styles.apiResponse}>
				<Text style={{ color: 'white', fontSize: 20 }}>
					{healthStatus == 2
						? 'Your Heart seems alright! :)'
						: healthStatus == 1
						? 'In ' + countdown + ' seconds we will notify the authorities!'
						: 'SOS: The authorities have been notified '}
				</Text>
			</View>
			{healthStatus == 1 ? (
				<View style={styles.apiResponse}>
					<Text style={{ color: 'white', fontSize: 18 }}>
						If it's a false alarm, please press the "Warn" button.
					</Text>
				</View>
			) : (
				<View></View>
			)}

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
		alignItems: 'center',
		justifyContent: 'center',
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
