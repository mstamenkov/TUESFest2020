//voltmeter//
int analogInput = 0;
float vout = 0.0;
float vin = 0.0;
float R1 = 600.0; // resistance of R1 (12K)
float R2 = 600.0; // resistance of R2 (4.7K)
int value = 0;
int started = 0;
int trigPin = 11;    // Trigger
int echoPin = 12;    // Echo
int trigPin_two = 9;    // Trigger
int echoPin_two = 10;
int trigPin_three = 4;    // Trigger
int echoPin_three = 8;
long duration, cm = 2000;
long cm_two;
long cm_three;

void setup() {
    // put your setup code here, to run once:
    Serial.begin(9600);
    pinMode(A5, INPUT); //voltmeter input
    pinMode(5, OUTPUT); //signal for relay
    digitalWrite(5, HIGH);
    pinMode(3, INPUT); //signal from back sidelight
    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
    pinMode(trigPin_two, OUTPUT);
    pinMode(echoPin_two, INPUT);
    pinMode(trigPin_three, OUTPUT);
    pinMode(echoPin_three, INPUT);
    pinMode(2, OUTPUT);
    pinMode(13, INPUT);
    pinMode(7,OUTPUT);
    digitalWrite(7,LOW);
}

void loop() {
    // put your main code here, to run repeatedly:
    Serial.println(voltmeter());
    //delay(100);
    if (voltmeter() >= 9.95) {
        if (digitalRead(3) == LOW) {
            //Serial.println("gabariti OFF");
            digitalWrite(5, HIGH);
        }
        else digitalWrite(5, LOW);
    }
    else digitalWrite(5, LOW);

    if (digitalRead(13) == HIGH) {
        //parktronik 1

        digitalWrite(trigPin, LOW);
        delayMicroseconds(5);
        digitalWrite(trigPin, HIGH);
        delayMicroseconds(10);
        digitalWrite(trigPin, LOW);


        pinMode(echoPin, INPUT);
        duration = pulseIn(echoPin, HIGH);

        // Convert the time into a distance
        //cm = (duration/2) / 29.1;     // Divide by 29.1 or multiply by 0.0343

        //Serial.print("one: ");
        //Serial.print(cm);
        //Serial.println("cm");

        //parktronik 2

        digitalWrite(trigPin_two, LOW);
        delayMicroseconds(5);
        digitalWrite(trigPin_two, HIGH);
        delayMicroseconds(10);
        digitalWrite(trigPin_two, LOW);


        pinMode(echoPin_two, INPUT);
        duration = pulseIn(echoPin_two, HIGH);

        // Convert the time into a distance
        cm_two = (duration / 2) / 29.1;   // Divide by 29.1 or multiply by 0.0343

        Serial.print("two: ");
        Serial.print(cm_two);
        Serial.println("cm");

        //parktronik 3

        digitalWrite(trigPin_three, LOW);
        delayMicroseconds(5);
        digitalWrite(trigPin_three, HIGH);
        delayMicroseconds(10);
        digitalWrite(trigPin_three, LOW);


        pinMode(echoPin_three, INPUT);
        duration = pulseIn(echoPin_three, HIGH);

        // Convert the time into a distance
        cm_three = (duration / 2) / 29.1;   // Divide by 29.1 or multiply by 0.0343


        Serial.print("three: ");
        Serial.print(cm_three);
        delay(100);
        Serial.println("cm");

        cm_two -= 10;
        cm_three -= 10;

        if ((cm < 70 && cm > 50) || (cm_two < 70 && cm_two > 50) || (cm_three < 70 && cm_three > 50)) {
            tone(2, 4000, 200);
            delay(200);
            Serial.println("parva dist");
            //noTone(2);

        }
        else if ((cm < 50 && cm > 30) || (cm_two < 50 && cm_two > 30) || (cm_three < 50 && cm_three > 30)) {
            tone(2, 4000,160);
            //delay(30);
            Serial.println("sredna dist");
            //noTone(2);

        }

        else if ((cm < 30) || (cm_two < 30) || (cm_three < 30)) {
            tone(2, 4000);
            //delay(50);
            //noTone(2);

        }
        else noTone(2);
        //delay(250);

    }
    else noTone(2);
}

float voltmeter()
{
    value = analogRead(A5);
    vout = (value * 5.0) / 1024.0;
    vin = vout / (R2 / (R1 + R2));
    if (vin < 0.09) {
        vin = 0.0; //statement to quash undesired reading !
    }
    return vin;
}
