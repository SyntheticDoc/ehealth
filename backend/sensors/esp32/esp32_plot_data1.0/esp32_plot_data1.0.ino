 
int sampling_rate = 20; //Set to a default value 
bool mv; 
int step; 
void setup() {
  Serial.begin(115200);
  pinMode(16, INPUT); // Setup for leads off detection LO +
  pinMode(15, INPUT); // Setup for leads off detection LO -
  Serial.println("ECG Module Test sampling every second "); 

  sampling_rate = get_sampling_rate(); 

  mv = in_mV(); 


}

void loop() {
  String data_string = "{"; 
  for (int i = 0; i<1000 ; i = i+sampling_rate){ 

    
    double AO_voltage = calculate_voltage(analogRead(14),4095); // 4095 is the max anlog read value of esp32 

    if(i!=0){data_string= data_string +" "; }
    data_string = data_string+ String(AO_voltage); 
      //data[i] = AO_voltage; 

    delay(sampling_rate); // delays 1 millisecond 

    }

  data_string = data_string+"}"; 
  Serial.println(data_string);  // plot/display data on serial plotter/monitor
  
  
}

double calculate_voltage(int input, int max_value) {// calculates the actual values (max anolog read for esp is 4095) 
  double mv = double(input)/double(max_value) ;
  mv = (mv*10); // scaled for max of 10 mV


  
  //Serial.println(mv);

  return double(mv);   // in mv and scaled down 
  }


int get_sampling_rate(){
Serial.println("Please choose  a sampling rate :"); 
while (Serial.available() == 0) {   }                                 
  int input; 
  input = Serial.parseInt();                                             
  char mist = Serial.read();                                                  // eliminate read-in of 0 from control character by timeout
  if ((input < 0) || (input > 100)) {                        
   Serial.print("you entered something below zero or something out of optimal range");
   Serial.println("Please chose something between 1 and 100 ms ");
  }                                         
                                                       
  else {
    Serial.println("Sampling Rate set to: "+ String(input)+"ms"); 
    return input; 

  }    
  

}

bool in_mV(){
  Serial.println("Please choose if the output sould be in mV, 1 for yes and any other value for no: "); 
  while (Serial.available() == 0) {}                                     
  int input; 
  input = Serial.parseInt();                                              // read integer value from serial monitor
  char mist = Serial.read();                                                  // eliminate read-in of 0 from control character by timeout
  if ((input !=1)) {                            // check if PWM 0 is out of usable range
   Serial.println("you entered something other than 1");
   Serial.println("The results will be dislayed as plain analog input");
   return (false); 
  }                                         
                                                       
  else {
    Serial.println("The plotted values will be computed to milliVolt" ); 
    return true; 

  }    

}


