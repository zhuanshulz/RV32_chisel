#include<string.h>
#include "my_printf.h"
int main()
{
	int n[10] = { 25,35,68,79,21,13,98,7,16,62 };//定义一个大小为10的数组
	int i, j, temp;
	my_printf("before bubble:\n");
	for (i = 0; i < 10; i++)
		my_printf("\t %d \t", n[i]);
	my_printf("\n");
	for (i = 1; i <= 9; i++)//外层循环是比较的轮数，数组内有10个数，那么就应该比较10-1=9轮
	{
		for (j = 0; j <= 9 - i; j++)//内层循环比较的是当前一轮的比较次数，例如：第一轮比较9-1=8次，第二轮比较9-2=7次
		{
			if (n[j] > n[j + 1])//相邻两个数如果逆序，则交换位置
			{
				temp = n[j];
				n[j] = n[j + 1];
				n[j + 1] = temp;
			}
		}
	}
	my_printf("after bubble:\n");
	for (i = 0; i < 10; i++)
		my_printf("\t %d \t", n[i]);
	my_printf("\n");
	return 0;
}
