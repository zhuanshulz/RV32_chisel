/*
 ****************************************************************************
 *
 *                   "DHRYSTONE" Benchmark Program
 *                   -----------------------------
 *                                                                            
 *  Version:    C, Version 2.1
 *                                                                            
 *  File:       dhry_1.c (part 2 of 3)
 *
 *  Date:       May 25, 1988
 *
 *  Author:     Reinhold P. Weicker
 *
 ****************************************************************************
 */


#include "dhry.h"
#include "my_printf.h"
#include <alloca.h>

/* Global Variables: */

Rec_Pointer     Ptr_Glob,
                Next_Ptr_Glob;
int             Int_Glob;
Boolean         Bool_Glob;
char            Ch_1_Glob,
                Ch_2_Glob;
int             Arr_1_Glob [50];
int             Arr_2_Glob [50] [50];

//extern char     *malloc ();
Enumeration     Func_1 ();
  /* forward declaration necessary since Enumeration may not simply be int */

#ifndef REG
        Boolean Reg = false;
#define REG
        /* REG becomes defined as empty */
        /* i.e. no register variables   */
#else
        Boolean Reg = true;
#endif

/* variables for time measurement: */

#ifdef TIMES
//struct tms      time_info;
//extern  int     times ();
                /* see library function "times" */
#define Too_Small_Time (2*HZ)
                /* Measurements should last at least about 2 seconds */
#endif
#ifdef TIME
extern long     time();
                /* see library function "time"  */
#define Too_Small_Time 2
                /* Measurements should last at least 2 seconds */
#endif
#ifdef MSC_CLOCK
extern clock_t	clock();
#define Too_Small_Time (2*HZ)
#endif

long            Begin_Time,
                End_Time,
                User_Time;
float           Microseconds,
                Dhrystones_Per_Second;

/* end of variables for time measurement */


main ()
/*****/
  /* main program, corresponds to procedures        */
  /* Main and Proc_0 in the Ada version             */
{

        One_Fifty       Int_1_Loc;
  REG   One_Fifty       Int_2_Loc;
        One_Fifty       Int_3_Loc;
  REG   char            Ch_Index;
        Enumeration     Enum_Loc;
        Str_30          Str_1_Loc;
        Str_30          Str_2_Loc;
  REG   int             Run_Index;
  REG   int             Number_Of_Runs;

  /* Initializations */

  Next_Ptr_Glob = (Rec_Pointer) alloca (sizeof (Rec_Type));
  Ptr_Glob = (Rec_Pointer) alloca (sizeof (Rec_Type));

  Ptr_Glob->Ptr_Comp                    = Next_Ptr_Glob;
  Ptr_Glob->Discr                       = Ident_1;
  Ptr_Glob->variant.var_1.Enum_Comp     = Ident_3;
  Ptr_Glob->variant.var_1.Int_Comp      = 40;
  strcpy (Ptr_Glob->variant.var_1.Str_Comp, 
          "DHRYSTONE PROGRAM, SOME STRING");
  strcpy (Str_1_Loc, "DHRYSTONE PROGRAM, 1'ST STRING");

  Arr_2_Glob [8][7] = 10;
        /* Was missing in published program. Without this statement,    */
        /* Arr_2_Glob [8][7] would have an undefined value.             */
        /* Warning: With 16-Bit processors and Number_Of_Runs > 32000,  */
        /* overflow may occur for this array element.                   */
  
  my_printf ("Dhrystone Benchmark, Version 2.1 (Language: C)\n");
  my_printf ("\n");
  if (Reg)
  {
    my_printf ("Program compiled with 'register' attribute\n");
    my_printf ("\n");
  }
  else
  {
    my_printf ("Program compiled without 'register' attribute\n");
    my_printf ("\n");
  }
//  printf ("Please give the number of runs through the benchmark: ");
  {
//    int n;
//    scanf ("%d", &n);
    Number_Of_Runs = 1;
  }
  my_printf ("\n");

  my_printf ("Execution starts, %d runs through Dhrystone\n", Number_Of_Runs);

  /***************/
  /* Start timer */
  /***************/
 
#ifdef TIMES
  //times (&time_info);
  //Begin_Time = (long) time_info.tms_utime;
  Begin_Time=0;
#endif
#ifdef TIME
  Begin_Time = time ( (long *) 0);
#endif
#ifdef MSC_CLOCK
  Begin_Time = clock();
#endif
  my_printf("Begin_Time\n");
  
  my_printf("%c",0x00);
  
	//*(char*)0xf0004='t';
  for (Run_Index = 1; Run_Index <= Number_Of_Runs; ++Run_Index)
  {
    //my_printf("point1\n");

    Proc_5();
    Proc_4();
      /* Ch_1_Glob == 'A', Ch_2_Glob == 'B', Bool_Glob == true */
    Int_1_Loc = 2;
    Int_2_Loc = 3;
    //my_printf("ABCDEFGH\n");
    strcpy (Str_2_Loc, "DHRYSTONE PROGRAM, 2'ND STRING");
    //strcpy (Str_2_Loc, "12345678");
    //my_printf("test,%s",Str_2_Loc);

    Enum_Loc = Ident_2;
    // my_printf("Bool_Glob=%d\n",Bool_Glob);
    // my_printf("Bool_Glob=%d\n",Func_2 (Str_1_Loc, Str_2_Loc));
    Bool_Glob = ! Func_2 (Str_1_Loc, Str_2_Loc);
      /* Bool_Glob == 1 */
     // my_printf("point2\n");
     //my_printf("HHHHHHHHHHHHHHHHHHHtest3: %d\n",Int_3_Loc);
    while (Int_1_Loc < Int_2_Loc)  /* loop body executed once */
    {
      Int_3_Loc = 5 * Int_1_Loc - Int_2_Loc;
      //my_printf("HHHHHHHHHHHHHHHHHHHtest3: %d,%d\n",Int_2_Loc,Int_3_Loc);
        /* Int_3_Loc == 7 */
      Proc_7 (Int_1_Loc, Int_2_Loc, &Int_3_Loc);
      //my_printf("HHHHHHHHHHHHHHHHHHHtest3: %d,%d\n",Int_2_Loc,Int_3_Loc);
        /* Int_3_Loc == 7 */
      Int_1_Loc += 1;
    } /* while */
      /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
	//*(char*)0xf0004='t';
	//my_printf ("Arr_2_Glob[8][7]:    %d\n", Arr_2_Glob[8][7]);
    Proc_8 (Arr_1_Glob, Arr_2_Glob, Int_1_Loc, Int_3_Loc);
    //my_printf("HHHHHHHHHHHHHHHHHHHtest3: %d\n",Int_3_Loc);
      /* Int_Glob == 5 */
    Proc_1 (Ptr_Glob);
    //my_printf("point3\n");
    for (Ch_Index = 'A'; Ch_Index <= Ch_2_Glob; ++Ch_Index)
                             /* loop body executed twice */
    {
      if (Enum_Loc == Func_1 (Ch_Index, 'C'))
          /* then, not executed */
        {
        Proc_6 (Ident_1, &Enum_Loc);
        strcpy (Str_2_Loc, "DHRYSTONE PROGRAM, 3'RD STRING");
        Int_2_Loc = Run_Index;
        Int_Glob = Run_Index;
        }
    }
    //my_printf("point4\n");
      /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
    //my_printf("HHHHHHHHHHHHHHHHHHHtest: %d,%d\n",Int_2_Loc,Int_1_Loc);
    Int_2_Loc = Int_2_Loc * Int_1_Loc;
    //my_printf("HHHHHHHHHHHHHHHHHHHtest: %d,%d\n",Int_2_Loc,Int_3_Loc);
    Int_1_Loc = Int_2_Loc / Int_3_Loc;
    //my_printf("test: %d\n",Int_1_Loc);
    Int_2_Loc = 7 * (Int_2_Loc - Int_3_Loc) - Int_1_Loc;
      /* Int_1_Loc == 1, Int_2_Loc == 13, Int_3_Loc == 7 */
    Proc_2 (&Int_1_Loc);
    //my_printf("test: %d\n",Int_1_Loc);
    //my_printf("point5\n");
      /* Int_1_Loc == 5 */

  } /* loop "for Run_Index" */
	//*(char*)0xf0004='t';
  /**************/
  /* Stop timer */
  /**************/
  my_printf("End_Time\n");
  //*(char*)0xd0580000=0xff;
  my_printf("%c",0x00);
  
#ifdef TIMES
  //times (&time_info);
  //End_Time = (long) time_info.tms_utime;
  End_Time=0;
#endif
#ifdef TIME
  End_Time = time ( (long *) 0);
#endif
#ifdef MSC_CLOCK
  End_Time = clock();
#endif

  my_printf ("Execution ends\n");
  my_printf ("\n");
  my_printf ("Final values of the variables used in the benchmark:\n");
  my_printf ("\n");
  my_printf ("Int_Glob:            %d\n", Int_Glob);
  my_printf ("        should be:   %d\n", 5);
  my_printf ("Bool_Glob:           %d\n", Bool_Glob);
  my_printf ("        should be:   %d\n", 1);
  my_printf ("Ch_1_Glob:           %c\n", Ch_1_Glob);
  my_printf ("        should be:   %c\n", 'A');
  my_printf ("Ch_2_Glob:           %c\n", Ch_2_Glob);
  my_printf ("        should be:   %c\n", 'B');
  my_printf ("Arr_1_Glob[8]:       %d\n", Arr_1_Glob[8]);
  my_printf ("        should be:   %d\n", 7);
  my_printf ("Arr_2_Glob[8][7]:    %d\n", Arr_2_Glob[8][7]);
  my_printf ("        should be:   Number_Of_Runs + 10\n");
  my_printf ("Ptr_Glob->\n");
  my_printf ("  Ptr_Comp:          %d\n", (int) Ptr_Glob->Ptr_Comp);
  my_printf ("        should be:   (implementation-dependent)\n");
  my_printf ("  Discr:             %d\n", Ptr_Glob->Discr);
  my_printf ("        should be:   %d\n", 0);
  my_printf ("  Enum_Comp:         %d\n", Ptr_Glob->variant.var_1.Enum_Comp);
  my_printf ("        should be:   %d\n", 2);
  my_printf ("  Int_Comp:          %d\n", Ptr_Glob->variant.var_1.Int_Comp);
  my_printf ("        should be:   %d\n", 17);
  my_printf ("  Str_Comp:          %s\n", Ptr_Glob->variant.var_1.Str_Comp);
  my_printf ("        should be:   DHRYSTONE PROGRAM, SOME STRING\n");
  my_printf ("Next_Ptr_Glob->\n");
  my_printf ("  Ptr_Comp:          %d\n", (int) Next_Ptr_Glob->Ptr_Comp);
  my_printf ("        should be:   (implementation-dependent), same as above\n");
  my_printf ("  Discr:             %d\n", Next_Ptr_Glob->Discr);
  my_printf ("        should be:   %d\n", 0);
  my_printf ("  Enum_Comp:         %d\n", Next_Ptr_Glob->variant.var_1.Enum_Comp);
  my_printf ("        should be:   %d\n", 1);
  my_printf ("  Int_Comp:          %d\n", Next_Ptr_Glob->variant.var_1.Int_Comp);
  my_printf ("        should be:   %d\n", 18);
  my_printf ("  Str_Comp:          %s\n",
                                Next_Ptr_Glob->variant.var_1.Str_Comp);
  my_printf ("        should be:   DHRYSTONE PROGRAM, SOME STRING\n");
  my_printf ("Int_1_Loc:           %d\n", Int_1_Loc);
  my_printf ("        should be:   %d\n", 5);
  my_printf ("Int_2_Loc:           %d\n", Int_2_Loc);
  my_printf ("        should be:   %d\n", 13);
  my_printf ("Int_3_Loc:           %d\n", Int_3_Loc);
  my_printf ("        should be:   %d\n", 7);
  my_printf ("Enum_Loc:            %d\n", Enum_Loc);
  my_printf ("        should be:   %d\n", 1);
  my_printf ("Str_1_Loc:           %s\n", Str_1_Loc);
  my_printf ("        should be:   DHRYSTONE PROGRAM, 1'ST STRING\n");
  my_printf ("Str_2_Loc:           %s\n", Str_2_Loc);
  my_printf ("        should be:   DHRYSTONE PROGRAM, 2'ND STRING\n");
  my_printf ("\n");

  User_Time = End_Time - Begin_Time;
  my_printf("%c",0xff);
  //my_printf ("End_time:%f \n", End_Time);
  //my_printf ("User_time:%f \n", User_Time*1000000000);
/*  if (User_Time < Too_Small_Time)
  {
    my_printf ("Measured time too small to obtain meaningful results\n");
    my_printf ("Please increase number of runs\n");
    my_printf ("\n");
  }
  else
  {
*/
/*#ifdef TIME
    Microseconds = (float) User_Time * Mic_secs_Per_Second 
                        / (float) Number_Of_Runs;
    Dhrystones_Per_Second = (float) Number_Of_Runs / (float) User_Time;
#else
    Microseconds = (float) User_Time * Mic_secs_Per_Second 
                        / ((float) HZ * ((float) Number_Of_Runs));
    Dhrystones_Per_Second = ((float) HZ * (float) Number_Of_Runs)
                        / (float) User_Time;
#endif
    
    //my_printf ("Microseconds for one run through Dhrystone: ");
    //my_printf ("%f \n", Microseconds);
    //my_printf ("Dhrystones per Second:                      ");
    //my_printf ("%f \n", Dhrystones_Per_Second);
    //my_printf ("\n");
*/  
}


Proc_1 (Ptr_Val_Par)
/******************/

REG Rec_Pointer Ptr_Val_Par;
    /* executed once */
{
  REG Rec_Pointer Next_Record = Ptr_Val_Par->Ptr_Comp;  
                                        /* == Ptr_Glob_Next */
  /* Local variable, initialized with Ptr_Val_Par->Ptr_Comp,    */
  /* corresponds to "rename" in Ada, "with" in Pascal           */
  
  structassign (*Ptr_Val_Par->Ptr_Comp, *Ptr_Glob); 
  Ptr_Val_Par->variant.var_1.Int_Comp = 5;
  Next_Record->variant.var_1.Int_Comp 
        = Ptr_Val_Par->variant.var_1.Int_Comp;
  Next_Record->Ptr_Comp = Ptr_Val_Par->Ptr_Comp;
  Proc_3 (&Next_Record->Ptr_Comp);
    /* Ptr_Val_Par->Ptr_Comp->Ptr_Comp 
                        == Ptr_Glob->Ptr_Comp */
  if (Next_Record->Discr == Ident_1)
    /* then, executed */
  {
    Next_Record->variant.var_1.Int_Comp = 6;
    Proc_6 (Ptr_Val_Par->variant.var_1.Enum_Comp, 
           &Next_Record->variant.var_1.Enum_Comp);
    Next_Record->Ptr_Comp = Ptr_Glob->Ptr_Comp;
    Proc_7 (Next_Record->variant.var_1.Int_Comp, 10, 
           &Next_Record->variant.var_1.Int_Comp);
  }
  else /* not executed */
    structassign (*Ptr_Val_Par, *Ptr_Val_Par->Ptr_Comp);
} /* Proc_1 */


Proc_2 (Int_Par_Ref)
/******************/
    /* executed once */
    /* *Int_Par_Ref == 1, becomes 4 */

One_Fifty   *Int_Par_Ref;
{
  One_Fifty  Int_Loc;  
  Enumeration   Enum_Loc;

  Int_Loc = *Int_Par_Ref + 10;
  do /* executed once */
    if (Ch_1_Glob == 'A')
      /* then, executed */
    {
      Int_Loc -= 1;
      *Int_Par_Ref = Int_Loc - Int_Glob;
      Enum_Loc = Ident_1;
    } /* if */
  while (Enum_Loc != Ident_1); /* true */
} /* Proc_2 */


Proc_3 (Ptr_Ref_Par)
/******************/
    /* executed once */
    /* Ptr_Ref_Par becomes Ptr_Glob */

Rec_Pointer *Ptr_Ref_Par;

{
  if (Ptr_Glob != Null)
    /* then, executed */
    *Ptr_Ref_Par = Ptr_Glob->Ptr_Comp;
  Proc_7 (10, Int_Glob, &Ptr_Glob->variant.var_1.Int_Comp);
} /* Proc_3 */


Proc_4 () /* without parameters */
/*******/
    /* executed once */
{
  Boolean Bool_Loc;

  Bool_Loc = Ch_1_Glob == 'A';
  Bool_Glob = Bool_Loc | Bool_Glob;
  Ch_2_Glob = 'B';
} /* Proc_4 */


Proc_5 () /* without parameters */
/*******/
    /* executed once */
{
  Ch_1_Glob = 'A';
  Bool_Glob = false;
} /* Proc_5 */


        /* Procedure for the assignment of structures,          */
        /* if the C compiler doesn't support this feature       */
#ifdef  NOSTRUCTASSIGN
memcpy (d, s, l)
register char   *d;
register char   *s;
register int    l;
{
        while (l--) *d++ = *s++;
}
#endif


