int mode =0;
int starts=1;
unsigned long time;
unsigned long time_stop;
unsigned long heater_start;
//voltmeter//
int analogInput = 0;
float vout = 0.0;
float vin = 0.0;
float R1 = 12000.0; // resistance of R1 (12K)
float R2 = 4700.0; // resistance of R2 (4.7K)
int value = 0;

void setup()
{
  pinMode(4, OUTPUT);
  pinMode(5, INPUT);
  pinMode(5, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(A0,INPUT);
  pinMode(0,OUTPUT);
  pinMode(3,OUTPUT);
  pinMode(2,INPUT);
  Serial.begin(9600);
}

void loop()
{
  if(digitalRead(5) == HIGH){
    digitalWrite(0,HIGH);
  	if(voltmeter() > 12.5){
      Serial.println(voltmeter());
      digitalWrite(0,LOW);
      digitalWrite(10,HIGH); //kontakt
      Serial.print("contact");
      delay(1000);
      while(starts <= 3){ //cikal za opit za zapalvane
        Serial.print("starting attempt: ");
        Serial.print(starts);

        Serial.print(starts);
        if(digitalRead(12) == HIGH){
          digitalWrite(4,HIGH);
          time_stop = millis() + 2000;
          while(1){
            time = millis();
            if(digitalRead(12) == LOW){
              starts = 4;
              heater_start = millis() + 5000;
              break;
            }

            if(time >= time_stop){
              break;
            }
          }
          digitalWrite(4,LOW);
          starts++;
        }
        if(digitalRead(12) == HIGH){
          //delay(15000);
          delay(5000);
        }
      }
      Serial.print("End of starting attempts");
      Serial.print(starts);
    }
    else{
  		Serial.println("Low voltage: ");
   		Serial.println(voltmeter());
      	digitalWrite(10, LOW);
      	delay(1000);
		exit(0);

  	}

    if(digitalRead(12) == LOW && starts == 5){

      if(voltmeter() < 13.5){
          Serial.println("Alternator fault");
          Serial.println("Shutting down engine...");
          digitalWrite(10, LOW);
          delay(1000);

          exit(0);

      }
  	}
  }
  
  if(digitalRead(5) == LOW){
    //Serial.print("vatre2");
  	digitalWrite(10,LOW);
    starts = 1;
    delay(100);
  }
  
  if(digitalRead(2) == HIGH && digitalRead(5) == HIGH){
    if(millis() >= heater_start){
    	digitalWrite(3,HIGH);
    }
  }
  
  if(digitalRead(2) == LOW){
  	digitalWrite(3,LOW);
  }
  
}

float voltmeter()
{
	value = analogRead(A0);
   	vout = (value * 5.0) / 1024.0;
   	vin = vout / (R2/(R1+R2)); 
   	if (vin<0.09){
   		vin=0.0;//statement to quash undesired reading !
    }
  return vin;
}
