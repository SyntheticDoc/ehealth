import {
	View,
	Text,
	StyleSheet,
	TouchableOpacity,
	TextInput,
} from 'react-native';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons/faArrowLeft';

const textSize = 30;
const SettingsScreen = ({ navigation }) => {
	return (
		<View style={styles.container}>
			<View style={styles.headerContainer}>
				<Text style={styles.heading}>settings</Text>
			</View>
			<View style={styles.form}>
				<Text style={styles.formInputTitle}>Name:</Text>
				<TextInput style={styles.formInput} />
				<Text style={styles.formInputTitle}>Adress:</Text>
				<TextInput style={styles.formInput} />
				<Text style={styles.formInputTitle}>City:</Text>
				<TextInput style={styles.formInput} />
				<Text style={styles.formInputTitle}>Entry:</Text>
				<TextInput style={styles.formInput} />
			</View>
			<TouchableOpacity
				style={styles.backButton}
				onPress={() => {
					navigation.navigate('Home');
				}}
			>
				<FontAwesomeIcon
					icon={faArrowLeft}
					style={{ color: '#fff' }}
					size={50}
				></FontAwesomeIcon>
			</TouchableOpacity>
		</View>
	);
};

export default SettingsScreen;

const styles = StyleSheet.create({
	container: {
		flex: 1,
		flexDirection: 'column',
		backgroundColor: '#F47174',
		color: 'white',
		alignItems: 'center',
		justifyContent: 'space-betwen',
	},
	form: {
		alignSelf: 'flex-start',
		marginLeft: 20,
		marginTop: 100,
	},
	heading: {
		color: 'white',
		fontSize: 50,
	},
	headerContainer: {
		marginTop: 100,
	},
	backButton: {
		position: 'absolute',
		bottom: 0,
		right: 0,
		margin: 60,
	},
	formInputTitle: {
		fontSize: 35,
		color: 'white',
		marginVertical: 5,
	},
	formInput: {
		fontSize: 35,
		backgroundColor: 'white',
	},
});
