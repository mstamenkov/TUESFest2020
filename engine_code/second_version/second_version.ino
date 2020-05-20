int starts = 1;
unsigned long time_stop;
unsigned long heater_start;
//voltmeter//
int analogInput = 0;
float vout = 0.0;
float vin = 0.0;
float R1 = 12000.0; // resistance of R1 (12K)
float R2 = 4700.0; // resistance of R2 (4.7K)
int value = 0;
int started = 0;

unsigned long heater_delay = 10000;

void setup()
{
    pinMode(5, INPUT); //kontakt remote signal
    pinMode(8, INPUT); //heater remote signal
    pinMode(3, OUTPUT); //heater
    digitalWrite(3, HIGH);
    pinMode(4, OUTPUT); //starter
    digitalWrite(4, HIGH);
    pinMode(10, OUTPUT); //kontakt
    digitalWrite(10, HIGH);
    pinMode(11, INPUT); //alternator lamp
    pinMode(13, OUTPUT); //for test purposes
    digitalWrite(13, HIGH);
    pinMode(A0, INPUT); //voltmeter input
    Serial.begin(9600);
}

void loop()
{
    Serial.println(digitalRead(11));
    if (digitalRead(8) == HIGH && started == 1 && heater_start <= millis()) {
        digitalWrite(3, LOW);
    } else {
        digitalWrite(3, HIGH);
    }
    if (digitalRead(5) == LOW) {
        Serial.println("stopped");
        digitalWrite(10, HIGH);
        digitalWrite(4, HIGH);
        started = 0;
        starts = 0;
        delay(1000);
    } else if (started == 0) {
        if (digitalRead(5) == HIGH) {
            digitalWrite(10, LOW); //kontakt
            delay(1000);
            engine_start();
        }
    }

}

float voltmeter()
{
    value = analogRead(A0);
    vout = (value * 5.0) / 1024.0;
    vin = vout / (R2 / (R1 + R2));
    if (vin < 0.09) {
        vin = 0.0; //statement to quash undesired reading !
    }
    return vin;
}

void engine_start() {
    Serial.println("Engine_start");
    Serial.println(digitalRead(11));
    digitalWrite(4, LOW);
    time_stop = millis() + 6000;

    while (starts < 3) {
        if (time_stop <= millis()) {
            starts++;
            time_stop = millis() + 6000;
            continue;
        }

        if (digitalRead(11) == LOW) {
            Serial.println("started");
            started = 1;
            starts = 4;

            digitalWrite(4, HIGH);
            heater_start = millis() + heater_delay;
            break;
        }
    }
}
