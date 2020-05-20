#include <msp430g2553.h>
#include <stdint.h>
#include <string.h>
#define ADC_CHANNELS 3
#define TXD BIT2
#define RXD BIT1
#define TXLED BIT6

unsigned int adc_values[ADC_CHANNELS];

char string[] =
        { "AT+CIPSTART=\"TCP\",\"78.130.176.59\",5000\r\nAT+GPS=1\r\nAT+GPSRD=0\r\nAT+CIPSEND=6\r\n100010\r\n" }; //AT Command for connection with server and sending module ID
char result[300];
char gpsInit[] = {"AT+CIPSEND=100\r\n"};
char gpsData[] = {""};
unsigned int index, startBit = 0;
unsigned int i; //Counter
int limit = 62;
char last;
int x, y, z;
int imu_delay, to_send, position, flagIMU, flagGPS = 0;

/**
 * main.c
 */

void ConfigureAdc(void)
{
    ADC10CTL1 = INCH_5 + ADC10DIV_0 + CONSEQ_3 + SHS_0; //Multi-channel repeated conversion starting from channel 5
    ADC10CTL0 = SREF_0 + ADC10SHT_2 + MSC + ADC10ON + ADC10IE;
    ADC10AE0 = BIT5 + BIT4 + BIT3; //activating ADC to selected pins
    ADC10DTC1 = ADC_CHANNELS; //ADC_CHANNELS defined to 3

}
void readIMU();
void sendIMU();
void sendGPS();
void main(void)
{
    WDTCTL = WDTPW + WDTHOLD; // Stop WDT
    if (CALBC1_1MHZ == 0xFF)   // If calibration constant erased
    {
        while (1);          // do not load, trap CPU!!
    }

    DCOCTL = 0;             // Select lowest DCOx and MODx settings
    BCSCTL1 = CALBC1_1MHZ;   // Set range
    DCOCTL = CALDCO_1MHZ;   // Set DCO step + modulation
    P2OUT &= 0;
    P2DIR &= 0;
    P1DIR &= 0;
    P1OUT &= 0;
    P1SEL |= BIT5; //ADC Input pin P1.5
    P1REN |= BIT3;  // Enable resistor on P1.3
    P1OUT &= ~BIT3; // Set P1.3 resistor to pull down
    P1REN |= BIT4;  // Enable resistor on P1.4
    P1OUT &= ~BIT4; // Set P1.4 resistor to pull down
    P1REN |= BIT5;  // Enable resistor on P1.5
    P1OUT &= ~BIT5; // Set P1.5 resistor to pull down
    P1SEL |= BIT1 + BIT2;  // P1.1 UCA0RXD input
    P1SEL2 |= BIT1 + BIT2;  // P1.2 UCA0TXD output

    //Configuring the UART

    UCA0CTL1 |= UCSSEL_2; // SMCLK
    UCA0BR0 = 0x08; // 1MHz 115200
    UCA0BR1 = 0x00; // 1MHz 115200
    UCA0MCTL = UCBRS2 + UCBRS0; // Modulation UCBRSx = 5
    UCA0CTL1 &= ~UCSWRST; // Initialize USCI state machine
    UC0IE |= UCA0RXIE; // Enable USCI_A0 RX interrupt
    UCA0STAT &= ~UCLISTEN;

    //Enabling the interrupts

    IE2 |= UCA0TXIE;                  // Enable the Transmit interrupt
    IE2 |= UCA0RXIE;                  // Enable the Receive  interrupt
    _BIS_SR(GIE);

    P2DIR |= BIT1 + BIT3 + BIT5; //set direction for pins P2.1, P2.3, P2.5(RGB LED)
    ConfigureAdc();
    UCA0CTL1 |= UCSSEL_2; // SMCLK
    UCA0BR0 = 0x08; // 1MHz 115200
    UCA0BR1 = 0x00; // 1MHz 115200
    UCA0MCTL = UCBRS2 + UCBRS0; // Modulation UCBRSx = 5
    UCA0CTL1 &= ~UCSWRST; // Initialize USCI state machine
    UC0IE |= UCA0RXIE; // Enable USCI_A0 RX interrupt

    limit = 76;
    __delay_cycles(99000);
    IE2 |= UCA0TXIE; // Enable the Transmit interrupt

    limit = sizeof string - 1;
    __delay_cycles(99000);
    IE2 |= UCA0TXIE;
    __enable_interrupt(); // Enable interrupts.
    while (1)
    {
        while (imu_delay < 100)
        { //check the imu sensor every 10 sec
            readIMU();
            __delay_cycles(1000);
            imu_delay++;
        }
        imu_delay = 0;
        if (to_send >= 10)
        {
            sendIMU();
            sendGPS();
            to_send = 0;
        }
        to_send++;
    }
}

void readIMU()
{
    ADC10CTL0 &= ~ENC;
    while (ADC10CTL1 & BUSY);
    ADC10SA = (uint32_t) adc_values;
    __delay_cycles(10000); // Wait for ADC Ref to settle
    ADC10CTL0 |= ENC + ADC10SC; // Sampling and conversion start
    __bis_SR_register(CPUOFF + GIE); // LPM0 with interrupts enabled
    x = adc_values[0] - 269;
    y = adc_values[1] - 269;
    z = adc_values[2] - 525;
    if (x > 17 || x < -17)
        {
            P2OUT |= BIT1;
            flagIMU = 1;
        }
        else
            P2OUT &= ~BIT1;
        if (y > 17 || y < -17)
        {
            P2OUT |= BIT3;
            flagIMU = 1;
        }
        else
            P2OUT &= ~BIT3;
        if (z > 17 || z < -17)
        {
            P2OUT |= BIT5;
            flagIMU = 1;
        }
        else
            P2OUT &= ~BIT5;
}
// ADC10 interrupt service routine

void sendIMU()
{
    flagGPS=0;
    if (flagIMU == 1)
        string[81] = '1';
    else
        string[81] = '0';
    flagIMU=0;

}

void sendGPS(){
    flagGPS = 0;
    memset(result, 0, sizeof result);
    i=0;
    IE2 |= UCA0RXIE;
    __delay_cycles(99000);
    string[59] = '1';
    startBit = 50;
    limit = 62;
    i=0;
    IE2 |= UCA0TXIE;
    __delay_cycles(99000);
    memset(result, 0, sizeof result);
    while(i < 100){
    }

    IE2 &= ~UCA0RXIE;
    string[59] = '0';
    startBit = 50;
    limit = 62;
    IE2 |= UCA0TXIE;
    __delay_cycles(99000);

    flagGPS = 1;

    startBit = 0;
    limit = 16;
    IE2 |= UCA0TXIE;
    __delay_cycles(999000);
    flagGPS =2;

    startBit = 0;
    limit = 100;
    unsigned int index = 81;
    for(unsigned int i=98;i>91;i--){
        result[i] = string[index];
        index--;
    }
    IE2 |= UCA0TXIE;
    __delay_cycles(999000);
}

#pragma vector=ADC10_VECTOR
__interrupt void ADC10_ISR(void)
{
    __bic_SR_register_on_exit(CPUOFF); // Return to active mode
}

#pragma vector=USCIAB0TX_VECTOR
__interrupt void USCI0TX_ISR(void)
{
    P1OUT |= BIT0;
    if(flagGPS==0)UCA0TXBUF = string[startBit++]; // TX next character
    else if(flagGPS == 1) UCA0TXBUF = gpsInit[startBit++];
    else UCA0TXBUF = result[startBit++];
    if (startBit >= limit)
    {
        UC0IE &= ~UCA0TXIE; // Disable USCI_A0 TX interrupt
    }
    P1OUT &= ~BIT0;
}

#pragma vector = USCIAB0RX_VECTOR
__interrupt void USCI0RX_ISR(void)
{
    if (i > 299)
    {
        UC0IE &= ~UCA0TXIE;
        i = 0;
    }
    result[i++] = UCA0RXBUF;

    IFG2 &= ~UCA0RXIFG; // Clear RX flag
}
