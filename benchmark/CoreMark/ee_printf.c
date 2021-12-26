/*
Copyright 2018 Embedded Microprocessor Benchmark Consortium (EEMBC)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

#include <coremark.h>
#include <stdarg.h>

void printf_num(unsigned int num);
void printf_deci(int dec);
void printf_char(int ch);
void printf_str(char* str);
void printf_float(double d);
void my_printf(char* str,...);

volatile char* outputAddr = (char*)0x7f030000;

void printf_num(unsigned int num){
	if(num!=0){
		printf_num(num/10);
		unsigned int num_char=num%10+48;
		*outputAddr=(char)num_char;
	}

}

void printf_deci(int dec){
	if(dec==0){
		*outputAddr='0';
		return;
	}
	if(dec<0){
		*outputAddr='-';
		dec=0 - dec;
	}
	printf_num(dec);
}

void printf_char(int ch){
	*outputAddr=(char)ch;
}

void printf_str(char* str){
	int i=0;
	for(i=0;str[i]!='\0';i++){
		*outputAddr=str[i];
	}
}

/*
void printf_float(double d){
	int temp=(int)d;
	printf_deci(temp);
	
	*outputAddr='.';
	d=d-temp;
	if(d==0){
		for(temp=0;temp<6;temp++){
			*outputAddr='0';
		}
	}
	else{
		d=(int)d*1000000;
		printf_deci(d);
	}
	
}
*/
int ee_printf(const char* str, ...){
	va_list va_ptr;
	va_start(va_ptr,str);
	int i;
	for(i=0;str[i]!='\0';i++){
		if(str[i]!='%'){
			*outputAddr=str[i];
			continue;
		}
		switch(str[++i]){
			case 'd':
				printf_deci(va_arg(va_ptr,int));	
				break;
			case 'c':
				printf_char(va_arg(va_ptr,int));
				break;
			case 's':
				printf_str(va_arg(va_ptr,char*));
				break;
			//case 'f':
			//	printf_float(va_arg(va_ptr,double));
			//	break;
			default:
				break;

		}
	}
	va_end(va_ptr);
	return 1;
}


