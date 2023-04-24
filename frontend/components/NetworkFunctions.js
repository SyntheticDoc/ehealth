const getData = () => {
	fetch('http://catfact.ninja/fact', { method: 'GET' })
		.then((response) => response.json())
		.then((responseJson) => {
			
			// setHealthStatus(JSON.stringify(responseJson));
		})
		.catch((error) => {
			//Error
			// Alert.alert(JSON.stringify(error));
			console.error(error);
		});
};
const castEmergencyCall = () => {
	fetch(
		'http://192.168.188.95:8080/user/sendsms?recipient=436649150335&message="Hey buddy, your heart is fine"',
		{ method: 'GET' }
	)
		.then((response) => console.log(response))
		.catch((error) => {
			//Error
			console.error(error);
		});
};

export { getData, castEmergencyCall };
