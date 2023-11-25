//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package Sintactico;



//#line 2 "grammar.y"
import java.util.Collections;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import Lexico.AnalizadorLexico;

import GCodigo.Tercetos;
import GCodigo.Scope;

import Tools.Logger;
import Tools.Tupla;
import Tools.TablaSimbolos;
import Tools.TablaTipos;
import Tools.TablaClases;
//#line 34 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short CLASS=257;
public final static short INTERFACE=258;
public final static short IMPLEMENT=259;
public final static short RETURN=260;
public final static short IF=261;
public final static short ELSE=262;
public final static short END_IF=263;
public final static short FOR=264;
public final static short IN=265;
public final static short RANGE=266;
public final static short IMPL=267;
public final static short PRINT=268;
public final static short TOD=269;
public final static short EQUAL_OPERATOR=270;
public final static short NOT_EQUAL_OPERATOR=271;
public final static short GREATER_THAN_OR_EQUAL_OPERATOR=272;
public final static short LESS_THAN_OR_EQUAL_OPERATOR=273;
public final static short MINUS_ASSIGN=274;
public final static short VOID=275;
public final static short LONG=276;
public final static short UINT=277;
public final static short DOUBLE=278;
public final static short CADENA=279;
public final static short ID=280;
public final static short CTE_DOUBLE=281;
public final static short CTE_UINT=282;
public final static short CTE_LONG=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    2,    2,    2,    2,    3,    3,
    7,    8,    8,   10,   10,   11,   12,   12,   12,   13,
   13,   17,   17,   18,   18,   18,   18,   18,   18,   18,
   19,   20,   14,   14,   22,   22,   24,   25,   25,   25,
   25,   26,   23,   23,   27,   29,   15,   15,   15,    9,
   31,   31,   31,    4,   33,   34,   34,   34,   34,   35,
   35,   36,   36,   36,   37,   38,   38,   38,    5,    5,
    5,    5,   40,   40,   40,   40,   41,   41,   42,   43,
   44,   46,   46,   46,   46,   48,   47,   45,   45,   49,
   49,   49,   49,   49,   49,   49,   50,   50,   50,   54,
   55,   55,   56,   56,   56,   57,   57,   57,   57,   57,
   21,   58,   58,   58,   59,   59,   59,   59,   60,   60,
   60,   60,   60,   60,   61,   61,   61,   61,   61,   53,
   53,   53,   53,   53,   53,   52,   52,   52,   52,   52,
   52,   52,   52,   16,   16,   64,   65,   65,   66,   66,
   67,   32,   30,   39,   62,   63,   51,   28,   28,   28,
   28,   28,   28,   28,   28,   28,   69,   69,   69,   69,
   69,   71,   69,   68,   68,   70,   70,    6,    6,   72,
   72,   72,   72,   72,   72,   73,   74,   74,   74,   74,
   74,   74,   81,   81,   81,   79,   79,   83,   83,   80,
   75,   75,   84,   84,   84,   84,   85,   85,   85,   76,
   87,   87,   87,   88,   88,   88,   86,   77,   89,   89,
   90,   90,   91,   91,   92,   94,   93,   82,   95,   95,
   78,   78,   78,   78,
};
final static short yylen[] = {                            2,
    3,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    1,    3,    3,    1,    2,    1,    1,    1,    1,    3,
    2,    1,    3,    1,    3,    4,    3,    3,    3,    3,
    1,    1,    3,    2,    2,    2,    1,    4,    3,    5,
    4,    1,    1,    1,    2,    1,    2,    4,    4,    2,
    1,    3,    3,    3,    1,    3,    3,    2,    2,    1,
    2,    1,    1,    1,    2,    3,    2,    3,    5,    6,
    6,    6,    3,    3,    2,    2,    1,    2,    1,    2,
    2,    4,    3,    5,    4,    2,    1,    1,    1,    3,
    3,    3,    3,    3,    3,    3,    1,    1,    1,    3,
    1,    1,    1,    3,    3,    1,    3,    3,    3,    3,
    1,    1,    3,    3,    1,    3,    3,    3,    1,    1,
    1,    1,    3,    2,    4,    4,    4,    3,    3,    1,
    1,    1,    2,    2,    2,    4,    3,    4,    3,    4,
    4,    5,    5,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    5,    6,    4,
    5,    3,    5,    4,    2,    2,    3,    2,    5,    6,
    4,    0,    6,    1,    2,    1,    2,    1,    1,    1,
    1,    1,    1,    1,    1,    3,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    2,    2,    1,    1,    1,
    5,    5,    3,    3,    3,    2,    1,    1,    1,    5,
    1,    1,    1,    1,    1,    1,    3,    6,    1,    2,
    7,    7,    1,    1,    1,    1,    1,    2,    1,    1,
    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    0,   37,
  150,  149,  151,    0,  130,  131,  132,    0,    0,    0,
  200,    0,    3,    5,    6,    7,    8,    0,    0,    0,
  193,  145,  198,    0,   97,    0,   99,    0,    0,    0,
    0,  144,  146,  147,  148,  178,  179,  188,  189,  190,
  192,  195,  194,  187,  191,    0,   11,    0,   55,    0,
    0,    0,    0,    0,  101,  219,  102,    0,    0,    0,
    0,  234,  133,  135,  134,    0,  165,  174,    0,    0,
  166,    0,    1,    4,    0,    0,   22,    0,   36,    0,
  230,  229,  228,   42,   35,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  196,  197,    0,
    0,    0,    9,    0,    0,    0,   54,    0,    0,    0,
  120,  121,  119,    0,    0,    0,    0,  115,  122,    0,
  206,    0,    0,    0,    0,    0,    0,  180,  181,  182,
  183,  184,  185,    0,    0,    0,  220,    0,    0,  154,
    0,  232,  231,  233,    0,    0,  162,  175,    0,    0,
  186,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   92,    0,   93,   94,   96,   95,   91,   90,  100,    0,
  137,   46,    0,    0,  139,    0,  152,    0,   51,  153,
    0,   14,   16,   17,   18,   19,    0,    0,    0,    0,
   10,   58,   64,    0,    0,    0,   60,   62,   63,   59,
    0,    0,    0,  124,    0,    0,    0,  205,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  204,  203,    0,
  168,    0,  176,   31,    0,    0,    0,    0,    0,    0,
    0,    0,  160,    0,    0,  164,    0,   23,    0,   27,
   32,   28,   30,   29,   25,    0,   39,    0,    0,  141,
    0,  136,  140,    0,  138,    0,    0,   12,   15,    0,
   44,    0,   43,    0,    0,   13,    0,    0,   56,   61,
   57,    0,  128,    0,  129,    0,  123,    0,    0,    0,
    0,    0,    0,    0,    0,  116,  117,  118,    0,    0,
  167,  177,  202,  201,  210,  215,  214,  216,  217,    0,
    0,    0,    0,    0,    0,    0,   69,  161,  158,    0,
  163,   26,   41,   45,    0,   38,  142,  143,   53,   52,
   20,   33,    0,    0,   66,   68,  127,  126,  125,  171,
    0,    0,  225,    0,  223,  224,  218,   75,    0,    0,
   77,   79,    0,   76,    0,   71,   72,   70,  159,   40,
   49,   48,  172,  169,    0,    0,    0,   87,   81,    0,
   73,   78,   89,   88,   80,   74,  173,  170,  227,    0,
    0,    0,    0,    0,    0,    0,   83,    0,    0,    0,
  226,    0,    0,   85,    0,    0,   82,  222,  221,   84,
};
final static short yydgoto[] = {                          3,
   22,   23,   24,   25,   26,   78,   58,  113,  114,  191,
  192,  193,  194,  195,  196,   28,   86,   87,   88,  250,
  251,   29,  272,   30,   89,   90,  259,   31,  183,   32,
  188,  189,   60,  117,  206,  207,  208,  209,  151,  314,
  350,  351,  352,  353,  375,  369,  370,  390,   33,   34,
  121,  122,  123,   38,   39,  124,  125,  172,  127,  128,
  129,   40,   41,   42,   43,   44,   45,   79,  135,  232,
  377,  233,   46,   47,   48,   49,   50,   51,   52,   53,
   54,   55,   56,   63,  144,  145,  146,  309,   68,  311,
  347,  344,  380,  392,   93,
};
final static short yysindex[] = {                      -110,
    0,  233,    0, -239, -221,   10, -182, -194,    3,    0,
    0,    0,    0,    0,    0,    0,    0, -133,  275,  328,
    0,  179,    0,    0,    0,    0,    0, -218,   13, -177,
    0,    0,    0,  792,    0,    0,    0,    0,  105,   31,
   33,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   49,    0,  -34,    0,   52,
  586,  448,  721, -187,    0,    0,    0, -124, -165,  116,
   57,    0,    0,    0,    0,  125,    0,    0,  404,  129,
    0,  431,    0,    0,    0,   67,    0,  576,    0,   79,
    0,    0,    0,    0,    0,  586,  586,  586,  586,  586,
  586,  586, -104,  -52,   69,  -42,   97,    0,    0,  -72,
  300,  300,    0,   80,  380,  188,    0,  165,    0,  519,
    0,    0,    0,  -71,   85,   82,  436,    0,    0,  144,
    0,   71,  590,  -64,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -156,  -45,  -41,    0,  -38,  221,    0,
  -47,    0,    0,    0,  459,  191,    0,    0,  252,  259,
    0,  -64,  235,  586,  586,  586,  586,  586,   65,  -22,
    0,   82,    0,    0,    0,    0,    0,    0,    0,  207,
    0,    0,    1,  234,    0,   14,    0,  100,    0,    0,
  666,    0,    0,    0,    0,    0, -218,   32,  178,  204,
    0,    0,    0,  -64, -177,  699,    0,    0,    0,    0,
  248,  -88,  477,    0,  333,  586,  586,    0,  586,  586,
  586,  586,  586,  586,  586,  586,  586,    0,    0,  357,
    0,  727,    0,    0,  375,  386,  397,  721,  402,  175,
  387,  -17,    0,  500,  529,    0,  409,    0,  586,    0,
    0,    0,    0,    0,    0,  327,    0,  -64,   19,    0,
  412,    0,    0,  417,    0,  -72,  -72,    0,    0,  268,
    0,  426,    0,  216,  218,    0,  418,  287,    0,    0,
    0,  360,    0,  445,    0,  446,    0,   85,   85,   82,
   82,   82,   82,  436,  436,    0,    0,    0,  750,  451,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -18,
  656, -109,   -8,  452,  175,  458,    0,    0,    0,  553,
    0,    0,    0,    0,  465,    0,    0,    0,    0,    0,
    0,    0,  433,  463,    0,    0,    0,    0,    0,    0,
  778,  853,    0,  311,    0,    0,    0,    0,  232,  -96,
    0,    0,   39,    0,   -6,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  881,  -18,  -18,    0,    0,  177,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  475,
  461,  265,  254,  -18,  -18,  408,    0,  257,  282,  501,
    0,  505,  508,    0,    0,  521,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  692,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  961,    0,  151,  373,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  629,    0,    0,  891,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  344,    0,
    0,    0,    0,    0,  -39,  124,  797,    0,    0,    0,
    0,    0,    0,    0,  146,  285,  305,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  875,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  231,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  283,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  804,
    0,  848,    0,  899,    0,    0,  910,  916,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   15,   53,  683,
  930,  953,  976,  820,  833,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   25,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  543,    0,    0,    0,    8,    0,  456,    0,  454,
  -95,    0,    0,    0,  -77,  -48,  -80,  410, -224, -119,
 1154,  -68,    0,  578,  -23,    0,  196,  -28,  464, 1100,
    0,   95,    0,    0,  467, -107,    0,    0,    0, -202,
  272, -178,    0,    0,    0,    0,    0,    0,    0,    0,
    2,    6,   -2,  574,    0,  524,  176,  988,  215,  186,
    0,    0,    0,    0,    0,    0,    0,   16, -193, -247,
    0,  -49,  -32,    0,  -58,  -51,  -31,  -12,   -5,   34,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  220,  205,    0,
};
final static int YYTABLESIZE=1367;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         37,
   92,  103,   72,   35,  138,  112,   95,   36,   66,   27,
  242,  139,    2,  136,  134,  348,   37,   37,  257,   37,
   35,   35,  313,   35,   36,   36,   18,   36,  371,   27,
  137,  140,  354,  324,  376,   82,  283,  203,  203,  317,
   57,  262,  198,  198,  306,  252,  253,  254,  255,   62,
  141,  341,   20,  218,  265,  104,   91,  142,   59,  326,
   37,   85,  197,  197,   35,   86,  204,  204,   36,   69,
  105,   20,  107,   64,  138,  271,   37,  147,   20,   37,
   35,  139,  373,   35,   36,  103,  158,   36,  111,  158,
  149,  116,  108,  105,  365,  269,  143,   65,  280,  235,
  153,  140,   94,  280,  269,  312,  236,  109,  120,  181,
  161,  229,  357,   18,  150,  154,  270,  345,  170,  112,
  141,  258,  198,  277,  223,  162,  224,  142,  203,  322,
   37,  198,   61,  203,   35,   19,  120,  185,   36,  104,
  148,   18,  197,  266,  221,    1,  222,   73,   74,   75,
  103,  197,   37,  104,   19,  106,   35,  204,  267,  152,
   36,   19,  204,  324,  106,   10,  143,  282,  155,  273,
  244,  372,  159,  138,  115,  179,  372,  105,   10,  138,
  139,  278,  302,  106,  228,  106,  139,  102,  307,  134,
  156,  102,  102,  102,  102,  102,  102,  102,  216,  217,
  140,  169,  111,  180,  213,  308,  140,  187,  241,  102,
  102,  102,  102,  184,  313,  234,  383,  237,   20,  141,
  238,  274,   21,   18,  110,  141,  142,  239,  210,   37,
  103,  103,  142,   35,  245,   37,  275,   36,  316,   35,
  138,   37,   37,   36,  276,   35,   35,  139,  106,   36,
   36,  158,  138,   11,   12,   13,  261,  190,   70,  139,
  320,  346,   15,   16,   17,  143,   10,  140,   10,  264,
   50,  143,   20,  156,  325,  102,   21,   18,  240,  140,
   31,   71,  138,  138,  104,  104,  141,  212,  281,  139,
  139,  302,  246,  142,  387,  249,   37,  312,  141,  382,
   35,   19,  247,   83,   36,  142,  138,  343,   37,  140,
  140,  331,   35,  139,   20,  302,   36,   37,   21,   18,
  256,   35,  105,  105,  374,   36,  162,  158,  141,  141,
  335,  260,  143,  140,  388,  142,  142,  118,   37,   37,
  216,  217,   35,   35,  143,  336,   36,   36,  119,   15,
   16,   17,  141,   50,  366,   19,  219,  220,  263,  142,
  329,  330,   37,  379,  379,  118,   35,   20,   81,  367,
   36,   21,   18,  287,  143,  143,  119,   15,   16,   17,
  101,  391,  391,  155,  101,  101,  101,  101,  101,  101,
  101,  288,  289,  106,  106,  106,  106,   19,  143,   77,
  299,  208,  101,  101,  101,  101,  102,  212,  208,  157,
  296,  297,  298,  157,  157,  157,  157,  157,  303,  157,
  102,  102,  102,  102,  102,  102,  102,  102,  102,  304,
  102,  157,  157,  157,  157,    4,    5,  294,  295,    6,
  305,  310,    7,   20,  315,    8,    9,   21,   18,  321,
   19,  323,  327,   10,   11,   12,   13,  328,   14,   15,
   16,   17,   10,   11,   12,   13,  155,  190,  101,  332,
   20,  333,  227,  334,   21,   18,  162,  225,   10,   11,
   12,   13,  226,  190,  337,  338,  339,  120,  131,    4,
    5,  361,   18,    6,  342,  356,    7,  157,   20,    8,
    9,  358,   21,   18,  202,  360,  362,   10,   11,   12,
   13,  368,   14,   15,   16,   17,  120,  285,  384,  385,
  386,   18,   10,   11,   12,   13,   19,  190,  157,   11,
   12,   13,  394,  190,   76,    6,  395,  396,    7,   20,
  207,  397,    9,   21,   18,  398,  211,  207,  399,   10,
   11,   12,   13,   19,   14,   15,   16,   17,  120,  214,
  209,  400,  145,   18,   84,  200,  213,  209,   20,  201,
  186,  248,   21,   18,   10,   11,   12,   13,  389,  190,
   67,   19,  211,  243,  355,  132,  381,   80,    6,  393,
    0,    7,   20,    0,    0,    9,   21,   18,    0,  101,
    0,    0,   10,   11,   12,   13,    0,   14,   15,   16,
   17,    0,    0,  101,  101,  101,  101,  101,  101,  101,
  101,  101,   19,  101,  318,  120,    0,    0,  157,    0,
   18,    0,    0,   21,   18,    0,  168,  157,    0,    0,
    0,    0,  157,  157,  157,  157,  157,  157,  157,  157,
  157,   19,  157,  319,   10,   11,   12,   13,    0,  190,
    0,    0,    0,  156,    6,    0,    0,    7,   42,   31,
    0,    9,   31,    0,    0,   19,    0,  359,   10,   11,
   12,   13,    0,   14,   15,   16,   17,   31,    0,   31,
  160,    6,  205,  205,    7,    0,    0,    0,    9,   21,
   18,    0,    0,  130,    0,   10,   11,   12,   13,    0,
   14,   15,   16,   17,  231,    0,  118,    0,    0,    6,
    0,    0,    7,  109,    0,    0,    9,  119,   15,   16,
   17,  155,  284,   10,   11,   12,   13,  101,   14,   15,
   16,   17,  109,    0,  109,  118,    0,    0,    0,    0,
    0,   42,  101,   31,    0,    0,  119,   15,   16,   17,
    6,    0,    0,    7,   21,   18,    0,    9,    0,    0,
   21,   18,    0,    0,   10,   11,   12,   13,  133,   14,
   15,   16,   17,  205,    0,    0,    0,  118,  205,    6,
  268,    0,    7,   21,   18,    0,    9,    0,  119,   15,
   16,   17,    0,   10,   11,   12,   13,  109,   14,   15,
   16,   17,    0,    6,  155,    0,    7,    0,    0,    0,
    9,   21,   18,  279,    0,    0,    0,   10,   11,   12,
   13,  163,   14,   15,   16,   17,    0,  112,    0,  112,
  112,  112,    0,  133,   21,  164,  165,  166,  167,  230,
    6,  301,  102,    7,  118,  112,  112,    9,  112,    0,
  113,    0,  113,  113,  113,  119,   15,   16,   17,  119,
   15,   16,   17,  114,  340,  114,  114,  114,  113,  113,
    0,  113,    0,    0,   31,    0,    0,    0,   34,  349,
  349,  114,  114,    0,  114,    0,   21,   18,   31,   31,
   31,   31,  363,   31,   31,   31,   31,    0,   31,    0,
    0,    0,    0,    0,    0,  111,    6,    0,  111,    7,
    0,  112,    0,    9,   21,   18,    0,  349,   21,    0,
    0,   24,  349,  111,   24,  119,   15,   16,   17,   47,
   10,   11,   12,   13,  113,  190,    0,  101,    0,   24,
   65,    0,  109,  109,  109,  109,   67,  114,    0,    0,
    0,  101,  101,  101,  101,  101,    0,    0,    0,    0,
  110,  153,   34,   10,   11,   12,   13,  364,  190,    0,
    0,    6,    0,    0,    7,    0,  300,    6,    9,  110,
    7,  110,    0,  107,    9,    0,   11,   12,   13,  111,
   14,   15,   16,   17,  199,  378,  119,   15,   16,   17,
    6,    0,  107,    7,  107,   24,  108,    9,    0,  199,
    0,   98,    0,   47,    0,    0,    0,    0,    0,  119,
   15,   16,   17,    0,   65,  108,    0,  108,    6,    0,
   67,    7,    0,    0,    0,    9,    0,   96,  126,  126,
    0,    0,  112,    0,  110,    0,    0,  119,   15,   16,
   17,   97,   98,   99,  100,  101,  112,  112,  112,  112,
    0,  112,  112,  112,  112,  113,  112,  107,   21,   21,
   21,   21,    0,   21,    0,    0,    0,    0,  114,  113,
  113,  113,  113,    0,  113,  113,  113,  113,    0,  113,
  108,    0,  114,  114,  114,  114,    0,  114,  114,  114,
  114,    0,  114,    6,    0,    0,    7,    0,    0,    0,
    9,    0,   34,   34,   34,   34,    0,   34,    0,    0,
  111,    0,  119,   15,   16,   17,    0,    0,    0,    0,
    0,    6,    0,    0,    7,    0,    0,    0,    9,  111,
  111,  111,  111,    0,  111,    0,    0,    0,    0,    0,
  119,   15,   16,   17,    0,   24,   24,   24,   24,    0,
   24,    0,    0,   47,   47,   47,   47,    0,   47,    0,
    0,    0,    0,    0,   65,   65,   65,   65,    0,   65,
   67,   67,   67,   67,    0,   67,    0,    0,    0,  110,
  110,  110,  110,  126,  126,    0,  290,  291,  292,  293,
  199,  199,    0,    0,  199,  199,   98,    0,    0,    0,
    0,    0,  107,  107,  107,  107,    0,    0,    0,    0,
   98,   98,   98,   98,   98,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  108,  108,  108,  108,  171,
  173,  174,  175,  176,  177,  178,    0,    0,  182,    0,
  182,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  215,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  199,    0,    0,    0,    0,    0,    0,    0,    0,  199,
    0,    0,    0,    0,    0,  199,    0,    0,    0,    0,
  199,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  286,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          2,
   29,   41,    0,    2,   63,   40,   30,    2,    7,    2,
   58,   63,  123,   63,   63,  125,   19,   20,   41,   22,
   19,   20,   40,   22,   19,   20,   45,   22,  125,   22,
   63,   63,   41,  258,   41,   20,  125,  115,  116,  242,
  280,   41,  111,  112,  238,  165,  166,  167,  168,   40,
   63,  299,   40,  125,   41,   41,   44,   63,  280,   41,
   63,  280,  111,  112,   63,   41,  115,  116,   63,  264,
   40,   40,   40,  256,  133,   44,   79,  265,   40,   82,
   79,  133,   44,   82,   79,  125,   79,   82,  123,   82,
  256,   40,   44,   41,  342,  191,   63,  280,  206,  256,
   44,  133,  280,  211,  200,  123,  263,   59,   40,   41,
   44,   41,  315,   45,  280,   59,  197,  311,   40,   40,
  133,  170,  191,  204,   43,   59,   45,  133,  206,  249,
  133,  200,  123,  211,  133,  123,   40,   41,  133,  125,
  265,   45,  191,   44,   60,  256,   62,  281,  282,  283,
   46,  200,  155,  123,  123,  123,  155,  206,   59,   44,
  155,  123,  211,  388,   41,  275,  133,  256,   44,  198,
  155,  350,   44,  232,  123,  280,  355,  125,  275,  238,
  232,  205,  232,   60,   41,   62,  238,   37,  238,  238,
   40,   41,   42,   43,   44,   45,   46,   47,  270,  271,
  232,  123,  123,  256,   40,  238,  238,  280,  256,   59,
   60,   61,   62,  256,   40,  280,   40,  263,   40,  232,
  262,   44,   44,   45,  259,  238,  232,  266,   41,  232,
  270,  271,  238,  232,   44,  238,   59,  232,  256,  238,
  299,  244,  245,  238,   41,  244,  245,  299,  125,  244,
  245,  244,  311,  276,  277,  278,  256,  280,  256,  311,
  245,  311,  281,  282,  283,  232,  275,  299,  275,  256,
   40,  238,   40,  123,  256,  125,   44,   45,   58,  311,
  256,  279,  341,  342,  270,  271,  299,  123,   41,  341,
  342,  341,   41,  299,   41,   61,  299,  123,  311,  123,
  299,  123,   44,  125,  299,  311,  365,  310,  311,  341,
  342,   44,  311,  365,   40,  365,  311,  320,   44,   45,
  256,  320,  270,  271,  353,  320,   59,  320,  341,  342,
   44,  125,  299,  365,  383,  341,  342,  269,  341,  342,
  270,  271,  341,  342,  311,   59,  341,  342,  280,  281,
  282,  283,  365,  123,   44,  123,  272,  273,  125,  365,
  266,  267,  365,  366,  367,  269,  365,   40,   41,   59,
  365,   44,   45,   41,  341,  342,  280,  281,  282,  283,
   37,  384,  385,   40,   41,   42,   43,   44,   45,   46,
   47,  216,  217,  270,  271,  272,  273,  123,  365,  125,
   44,  256,   59,   60,   61,   62,  256,  262,  263,   37,
  225,  226,  227,   41,   42,   43,   44,   45,   44,   47,
  270,  271,  272,  273,  274,  275,  276,  277,  278,   44,
  280,   59,   60,   61,   62,  257,  258,  223,  224,  261,
   44,   40,  264,   40,   58,  267,  268,   44,   45,   41,
  123,  125,   41,  275,  276,  277,  278,   41,  280,  281,
  282,  283,  275,  276,  277,  278,  123,  280,  125,   44,
   40,  256,   37,  256,   44,   45,   59,   42,  275,  276,
  277,  278,   47,  280,  125,   41,   41,   40,   41,  257,
  258,   59,   45,  261,   44,   44,  264,  125,   40,  267,
  268,   44,   44,   45,  125,   41,   44,  275,  276,  277,
  278,  280,  280,  281,  282,  283,   40,   41,   44,   59,
  256,   45,  275,  276,  277,  278,  123,  280,  125,  276,
  277,  278,  125,  280,  260,  261,  280,  256,  264,   40,
  256,   41,  268,   44,   45,   41,  262,  263,   41,  275,
  276,  277,  278,  123,  280,  281,  282,  283,   40,   41,
  256,   41,  280,   45,   22,  112,  262,  263,   40,  114,
  107,  162,   44,   45,  275,  276,  277,  278,  383,  280,
    7,  123,  116,  125,  313,   62,  367,  260,  261,  385,
   -1,  264,   40,   -1,   -1,  268,   44,   45,   -1,  256,
   -1,   -1,  275,  276,  277,  278,   -1,  280,  281,  282,
  283,   -1,   -1,  270,  271,  272,  273,  274,  275,  276,
  277,  278,  123,  280,  125,   40,   -1,   -1,  256,   -1,
   45,   -1,   -1,   44,   45,   -1,   61,  265,   -1,   -1,
   -1,   -1,  270,  271,  272,  273,  274,  275,  276,  277,
  278,  123,  280,  125,  275,  276,  277,  278,   -1,  280,
   -1,   -1,   -1,  260,  261,   -1,   -1,  264,   40,   41,
   -1,  268,   44,   -1,   -1,  123,   -1,  125,  275,  276,
  277,  278,   -1,  280,  281,  282,  283,   59,   -1,   61,
  260,  261,  115,  116,  264,   -1,   -1,   -1,  268,   44,
   45,   -1,   -1,  256,   -1,  275,  276,  277,  278,   -1,
  280,  281,  282,  283,  125,   -1,  269,   -1,   -1,  261,
   -1,   -1,  264,   41,   -1,   -1,  268,  280,  281,  282,
  283,   40,  256,  275,  276,  277,  278,   46,  280,  281,
  282,  283,   60,   -1,   62,  269,   -1,   -1,   -1,   -1,
   -1,  123,   61,  125,   -1,   -1,  280,  281,  282,  283,
  261,   -1,   -1,  264,   44,   45,   -1,  268,   -1,   -1,
   44,   45,   -1,   -1,  275,  276,  277,  278,  123,  280,
  281,  282,  283,  206,   -1,   -1,   -1,  269,  211,  261,
  125,   -1,  264,   44,   45,   -1,  268,   -1,  280,  281,
  282,  283,   -1,  275,  276,  277,  278,  125,  280,  281,
  282,  283,   -1,  261,  123,   -1,  264,   -1,   -1,   -1,
  268,   44,   45,  125,   -1,   -1,   -1,  275,  276,  277,
  278,  256,  280,  281,  282,  283,   -1,   41,   -1,   43,
   44,   45,   -1,  123,   41,  270,  271,  272,  273,  260,
  261,  125,   61,  264,  269,   59,   60,  268,   62,   -1,
   41,   -1,   43,   44,   45,  280,  281,  282,  283,  280,
  281,  282,  283,   41,  125,   43,   44,   45,   59,   60,
   -1,   62,   -1,   -1,  256,   -1,   -1,   -1,   41,  312,
  313,   59,   60,   -1,   62,   -1,   44,   45,  270,  271,
  272,  273,  125,  275,  276,  277,  278,   -1,  280,   -1,
   -1,   -1,   -1,   -1,   -1,   41,  261,   -1,   44,  264,
   -1,  125,   -1,  268,   44,   45,   -1,  350,  125,   -1,
   -1,   41,  355,   59,   44,  280,  281,  282,  283,   41,
  275,  276,  277,  278,  125,  280,   -1,  256,   -1,   59,
   41,   -1,  270,  271,  272,  273,   41,  125,   -1,   -1,
   -1,  270,  271,  272,  273,  274,   -1,   -1,   -1,   -1,
   41,  280,  125,  275,  276,  277,  278,  125,  280,   -1,
   -1,  261,   -1,   -1,  264,   -1,  260,  261,  268,   60,
  264,   62,   -1,   41,  268,   -1,  276,  277,  278,  125,
  280,  281,  282,  283,   44,  125,  280,  281,  282,  283,
  261,   -1,   60,  264,   62,  125,   41,  268,   -1,   59,
   -1,   61,   -1,  125,   -1,   -1,   -1,   -1,   -1,  280,
  281,  282,  283,   -1,  125,   60,   -1,   62,  261,   -1,
  125,  264,   -1,   -1,   -1,  268,   -1,  256,   61,   62,
   -1,   -1,  256,   -1,  125,   -1,   -1,  280,  281,  282,
  283,  270,  271,  272,  273,  274,  270,  271,  272,  273,
   -1,  275,  276,  277,  278,  256,  280,  125,  275,  276,
  277,  278,   -1,  280,   -1,   -1,   -1,   -1,  256,  270,
  271,  272,  273,   -1,  275,  276,  277,  278,   -1,  280,
  125,   -1,  270,  271,  272,  273,   -1,  275,  276,  277,
  278,   -1,  280,  261,   -1,   -1,  264,   -1,   -1,   -1,
  268,   -1,  275,  276,  277,  278,   -1,  280,   -1,   -1,
  256,   -1,  280,  281,  282,  283,   -1,   -1,   -1,   -1,
   -1,  261,   -1,   -1,  264,   -1,   -1,   -1,  268,  275,
  276,  277,  278,   -1,  280,   -1,   -1,   -1,   -1,   -1,
  280,  281,  282,  283,   -1,  275,  276,  277,  278,   -1,
  280,   -1,   -1,  275,  276,  277,  278,   -1,  280,   -1,
   -1,   -1,   -1,   -1,  275,  276,  277,  278,   -1,  280,
  275,  276,  277,  278,   -1,  280,   -1,   -1,   -1,  270,
  271,  272,  273,  216,  217,   -1,  219,  220,  221,  222,
  111,  112,   -1,   -1,  115,  116,  256,   -1,   -1,   -1,
   -1,   -1,  270,  271,  272,  273,   -1,   -1,   -1,   -1,
  270,  271,  272,  273,  274,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  270,  271,  272,  273,   96,
   97,   98,   99,  100,  101,  102,   -1,   -1,  105,   -1,
  107,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  120,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  191,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  200,
   -1,   -1,   -1,   -1,   -1,  206,   -1,   -1,   -1,   -1,
  211,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  213,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=283;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"CLASS","INTERFACE","IMPLEMENT",
"RETURN","IF","ELSE","END_IF","FOR","IN","RANGE","IMPL","PRINT","TOD",
"EQUAL_OPERATOR","NOT_EQUAL_OPERATOR","GREATER_THAN_OR_EQUAL_OPERATOR",
"LESS_THAN_OR_EQUAL_OPERATOR","MINUS_ASSIGN","VOID","LONG","UINT","DOUBLE",
"CADENA","ID","CTE_DOUBLE","CTE_UINT","CTE_LONG",
};
final static String yyrule[] = {
"$accept : program",
"program : '{' type_declarations '}'",
"program : error",
"type_declarations : type_declaration",
"type_declarations : type_declarations type_declaration",
"type_declaration : class_declaration",
"type_declaration : interface_declaration",
"type_declaration : implement_for_declaration",
"type_declaration : block_statement",
"class_declaration : CLASS class_name class_body",
"class_declaration : CLASS class_name interfaces class_body",
"class_name : ID",
"class_body : '{' class_body_declarations '}'",
"class_body : '(' class_body_declarations ')'",
"class_body_declarations : class_body_declaration",
"class_body_declarations : class_body_declarations class_body_declaration",
"class_body_declaration : class_member_declaration",
"class_member_declaration : field_declaration",
"class_member_declaration : method_declaration",
"class_member_declaration : inheritance_declaration",
"field_declaration : type variable_declarators ','",
"field_declaration : type variable_declarators",
"variable_declarators : variable_declarator",
"variable_declarators : variable_declarators ';' variable_declarator",
"variable_declarator : variable_declarator_id",
"variable_declarator : variable_declarator_id '=' variable_initializer",
"variable_declarator : variable_declarator_id error '=' variable_initializer",
"variable_declarator : variable_declarator_id EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id NOT_EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id LESS_THAN_OR_EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id GREATER_THAN_OR_EQUAL_OPERATOR variable_initializer",
"variable_declarator_id : ID",
"variable_initializer : arithmetic_operation",
"method_declaration : method_header method_body ','",
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"method_header : type method_declarator",
"result_type : VOID",
"method_declarator : method_name '(' formal_parameter ')'",
"method_declarator : method_name '(' ')'",
"method_declarator : method_name '(' formal_parameter error ')'",
"method_declarator : method_name '{' error '}'",
"method_name : ID",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : arithmetic_operation",
"inheritance_declaration : class_type ','",
"inheritance_declaration : class_type ';' error ','",
"inheritance_declaration : class_type ',' error ';'",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : reference_interface",
"interface_type_list : interface_type_list ';' reference_interface",
"interface_type_list : interface_type_list ',' reference_interface",
"interface_declaration : INTERFACE interface_name interface_body",
"interface_name : ID",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '(' interface_member_declaration ')'",
"interface_body : '{' '}'",
"interface_body : '(' ')'",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : constant_declaration",
"interface_method_declaration : abstract_method_declaration",
"interface_method_declaration : inheritance_declaration",
"constant_declaration : type variable_declarators",
"abstract_method_declaration : result_type method_declarator ','",
"abstract_method_declaration : result_type method_declarator",
"abstract_method_declaration : result_type method_declarator ';'",
"implement_for_declaration : IMPL FOR reference_class ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_class ':' error ','",
"implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
"implement_for_declaration : IMPL FOR reference_class error ':' implement_for_body",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body : '(' implement_for_body_declarations ')'",
"implement_for_body : '{' '}'",
"implement_for_body : '(' ')'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : impl_for_method_header implement_for_method_body",
"impl_for_method_header : result_type impl_for_method_declarator",
"impl_for_method_declarator : impl_method_name '(' impl_formal_parameter ')'",
"impl_for_method_declarator : impl_method_name '(' ')'",
"impl_for_method_declarator : impl_method_name '(' formal_parameter error ')'",
"impl_for_method_declarator : impl_method_name '{' error '}'",
"impl_formal_parameter : type ID",
"impl_method_name : ID",
"implement_for_method_body : block",
"implement_for_method_body : ','",
"assignment : left_hand_side '=' arithmetic_operation",
"assignment : left_hand_side MINUS_ASSIGN arithmetic_operation",
"assignment : left_hand_side error arithmetic_operation",
"assignment : left_hand_side EQUAL_OPERATOR arithmetic_operation",
"assignment : left_hand_side NOT_EQUAL_OPERATOR arithmetic_operation",
"assignment : left_hand_side LESS_THAN_OR_EQUAL_OPERATOR arithmetic_operation",
"assignment : left_hand_side GREATER_THAN_OR_EQUAL_OPERATOR arithmetic_operation",
"left_hand_side : reference_type",
"left_hand_side : invocation",
"left_hand_side : factor",
"field_acces : primary '.' ID",
"primary : ID",
"primary : field_acces",
"equality_expression : relational_expression",
"equality_expression : equality_expression EQUAL_OPERATOR relational_expression",
"equality_expression : equality_expression NOT_EQUAL_OPERATOR relational_expression",
"relational_expression : additive_expression",
"relational_expression : relational_expression '<' additive_expression",
"relational_expression : relational_expression '>' additive_expression",
"relational_expression : relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression",
"relational_expression : relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression",
"arithmetic_operation : additive_expression",
"additive_expression : multiplicative_expression",
"additive_expression : additive_expression '+' multiplicative_expression",
"additive_expression : additive_expression '-' multiplicative_expression",
"multiplicative_expression : unary_expression",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : factor",
"unary_expression : reference_type",
"unary_expression : invocation",
"unary_expression : conversion_expression",
"unary_expression : '(' arithmetic_operation ')'",
"unary_expression : '(' ')'",
"conversion_expression : TOD '(' arithmetic_operation ')'",
"conversion_expression : TOD '(' error ')'",
"conversion_expression : TOD '{' error '}'",
"conversion_expression : TOD '{' '}'",
"conversion_expression : TOD '(' ')'",
"factor : CTE_DOUBLE",
"factor : CTE_UINT",
"factor : CTE_LONG",
"factor : '-' CTE_DOUBLE",
"factor : '-' CTE_LONG",
"factor : '-' CTE_UINT",
"invocation : reference_function '(' real_parameter ')'",
"invocation : reference_function '(' ')'",
"invocation : reference_method '(' real_parameter ')'",
"invocation : reference_method '(' ')'",
"invocation : reference_method '{' error '}'",
"invocation : reference_function '{' error '}'",
"invocation : reference_function '(' real_parameter error ')'",
"invocation : reference_method '(' real_parameter error ')'",
"type : primitive_type",
"type : class_type",
"primitive_type : numeric_type",
"numeric_type : integral_type",
"numeric_type : floating_type",
"integral_type : UINT",
"integral_type : LONG",
"floating_type : DOUBLE",
"reference_interface : ID",
"class_type : ID",
"reference_class : ID",
"reference_function : ID",
"reference_method : field_acces",
"reference_type : primary",
"block : '{' block_statements RETURN ',' '}'",
"block : '{' block_statements RETURN ',' block_statements '}'",
"block : '{' RETURN ',' '}'",
"block : '{' RETURN ',' block_statements '}'",
"block : '{' block_statements '}'",
"block : '(' block_statements RETURN ',' ')'",
"block : '(' RETURN ',' ')'",
"block : '{' '}'",
"block : '(' ')'",
"executable_block : '{' executable_block_statements '}'",
"executable_block : '{' '}'",
"executable_block : '{' executable_block_statements RETURN ',' '}'",
"executable_block : '{' executable_block_statements RETURN ',' executable_block_statements '}'",
"executable_block : '{' RETURN ',' '}'",
"$$1 :",
"executable_block : '{' RETURN ',' executable_block_statements '}' $$1",
"block_statements : block_statement",
"block_statements : block_statements block_statement",
"executable_block_statements : executable_statement",
"executable_block_statements : executable_block_statements executable_statement",
"block_statement : local_variable_declaration",
"block_statement : statement",
"executable_statement : if_then_declaration",
"executable_statement : if_then_else_declaration",
"executable_statement : for_in_range_statement",
"executable_statement : print_statement",
"executable_statement : expression_statement",
"executable_statement : empty_statement",
"local_variable_declaration : type variable_declarators ','",
"statement : statement_without_trailing_substatement",
"statement : if_then_declaration",
"statement : if_then_else_declaration",
"statement : for_in_range_statement",
"statement : function_declaration",
"statement : print_statement",
"statement_without_trailing_substatement : block",
"statement_without_trailing_substatement : empty_statement",
"statement_without_trailing_substatement : expression_statement",
"expression_statement : statement_expression ','",
"expression_statement : statement_expression ';'",
"statement_expression : assignment",
"statement_expression : invocation",
"empty_statement : ','",
"if_then_declaration : IF if_then_cond if_then_body END_IF ','",
"if_then_declaration : IF if_then_cond if_then_body error ','",
"if_then_cond : '(' equality_expression ')'",
"if_then_cond : '(' error ')'",
"if_then_cond : '{' equality_expression '}'",
"if_then_cond : '(' ')'",
"if_then_body : executable_statement",
"if_then_body : executable_block",
"if_then_body : local_variable_declaration",
"if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ','",
"if_else_then_body : executable_statement",
"if_else_then_body : executable_block",
"if_else_then_body : local_variable_declaration",
"if_else_body : executable_statement",
"if_else_body : executable_block",
"if_else_body : local_variable_declaration",
"if_then_else_body : if_else_then_body ELSE if_else_body",
"for_in_range_statement : FOR for_in_range_initializer IN RANGE for_in_range_cond for_in_range_body",
"for_in_range_initializer : reference_type",
"for_in_range_initializer : error IN",
"for_in_range_cond : '(' for_init ';' for_end ';' for_update ')'",
"for_in_range_cond : '(' for_init ',' for_end ',' for_update ')'",
"for_in_range_body : executable_block",
"for_in_range_body : executable_statement",
"for_init : factor",
"for_update : factor",
"for_end : factor",
"function_declaration : method_header method_body_without_prototype",
"method_body_without_prototype : block",
"method_body_without_prototype : ','",
"print_statement : PRINT CADENA ','",
"print_statement : PRINT error ','",
"print_statement : PRINT CADENA ';'",
"print_statement : PRINT '\\000'",
};

//#line 890 "grammar.y"

private static AnalizadorLexico aLexico;
private static Tercetos tercetos;
private static Scope scope;
private static int yylval_recognition = 0;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
  Tupla<String, Short> t = aLexico.generateToken();
  String lexema = t.getFirst();
  Short token = t.getSecond();

  if (lexema != null){
    yylval = new ParserVal(lexema);
    yylval_recognition += 1;
  }
  
  return token;
}

// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println("Error en el parser: " + msg + " in " + val_peek(0).sval);
}


// ###############################################################
// metodos de lectura de los programadas
// ###############################################################

private static ArrayList<String> listFilesInDirectory(String path) {
  // Obtén el directorio actual
  File element = new File(System.getProperty("user.dir") + "/" + path);

  // Verifica si es un directorio o archivo válido
  if (element.isDirectory() || element.isFile()) {
    // Lista de archivos y directorios en el directorio actual
    File[] filesAndDirs = element.listFiles();
    ArrayList<String> out = new ArrayList<>();

    for (File fileOrDir : filesAndDirs) {
      out.add(fileOrDir.getName());
    }

    Collections.sort(out);

    // Itera a través de los archivos y directorios
    int i = 0;
    for (String name : out) {
      System.out.println("[" + i + "]" + ": " + name);
      i++;
    }

    return out;
  } else {
    System.err.println("f es un directorio válido.");
  }

  return null;
}

private static String generatePath() {
  ArrayList<String> directories = listFilesInDirectory("sample_programs");
  String path = "";

  if (!directories.isEmpty()) {
    Scanner scanner = new Scanner(System.in);
    int indice = -1;

    while (indice < 0) {
      System.out.print("Ingrese el numero de carpeta a acceder: ");
      String input = scanner.nextLine();
   
      try {
        indice = Integer.parseInt(input);
      } catch (Exception ex) {
        indice = -1;
      } 

      if (indice < directories.size() && indice >= 0) {
        path = directories.get(indice);
        directories = listFilesInDirectory("sample_programs" + "/" + path);
      } else {
        System.out.println("El indice no es correcto, ingrese nuevamente...");
        indice = -1;
      }

    }

    if (!directories.isEmpty()) {
      indice = -1;

      while (indice < 0) {

        System.out.print("Ingrese el numero de archivo binario a compilar: ");
        String input = scanner.nextLine();

        try {
          indice = Integer.parseInt(input);
        } catch (Exception ex) {
          indice = -1;
        } 

        if (indice < directories.size() && indice >= 0) {
          path += "/" + directories.get(Integer.parseInt(input));
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }
    }
    scanner.close();
  }
  return path;
}

public static void main (String [] args) throws IOException {
    System.out.println("Iniciando compilacion...");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() )
        return;

    Parser aSintactico = new Parser();
    scope = new Scope();
    tercetos = new Tercetos();
    scope.addObserver(tercetos); //Añado al terceto para avisarle cuando cambio de scope

    aSintactico.run();

    //Borrado de los identificadores que quedan sin ambito
    TablaSimbolos.purge();

    //aSintactico.dump_stacks(yylval_recognition);
    System.out.println(Logger.dumpLog());

    if (!Logger.errorsOcurred()){
      System.out.println("No se produjeron errores.\n"); //Para la parte 4, generacion de codigo maquina
      tercetos.printRules();
    } else 
      System.out.println("Se produjeron errores.\n");
    

    System.out.println(aLexico.getProgram());
}

//#line 1035 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 2:
//#line 40 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se reconocio el programa.");}
break;
case 5:
//#line 52 "grammar.y"
{scope.reset(); if (!val_peek(0).sval.isEmpty()) scope.changeScope(val_peek(0).sval);}
break;
case 6:
//#line 53 "grammar.y"
{scope.reset(); if (!val_peek(0).sval.isEmpty()) scope.changeScope(val_peek(0).sval);}
break;
case 7:
//#line 54 "grammar.y"
{scope.reset();}
break;
case 8:
//#line 55 "grammar.y"
{scope.reset();}
break;
case 9:
//#line 58 "grammar.y"
{
                        yyval = val_peek(1);
                        if (!val_peek(1).sval.isEmpty()){
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una clase.");

                          String error = TablaClases.chequeoAtributoSobreescrito(val_peek(1).sval);
                          if (error != null) 
                            Logger.logError(aLexico.getProgramPosition(), error);
                        }
                    }
break;
case 10:
//#line 68 "grammar.y"
{
                        yyval = val_peek(2);
                        if (!val_peek(2).sval.isEmpty()){
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");

                          String error = TablaClases.chequeoAtributoSobreescrito(val_peek(2).sval);
                          if (error != null) 
                            Logger.logError(aLexico.getProgramPosition(), error);
                          if (!val_peek(1).sval.isEmpty()) {
                            if (TablaClases.implementaMetodosInterfaz(val_peek(2).sval,val_peek(1).sval))
                              Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface e implementa todos sus metodos.");
                            else
                              Logger.logError(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface y NO implementa todos sus metodos.");
                          }

                        }            
                    }
break;
case 11:
//#line 87 "grammar.y"
{
              if(!scope.isDeclaredInMyScope(val_peek(0).sval)){
                TablaSimbolos.addClase(val_peek(0).sval); 
                TablaClases.addClase(val_peek(0).sval);
                yyval = new ParserVal(val_peek(0).sval);
              } else {
                Logger.logError(aLexico.getProgramPosition(), "La clase " + val_peek(0).sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                yyval = new ParserVal("");
              }
                scope.stack(val_peek(0).sval); 
            }
break;
case 13:
//#line 101 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de una clase debe estar delimitado por llaves \"{...}\".");}
break;
case 20:
//#line 116 "grammar.y"
{
                      if (!(val_peek(2).sval.isEmpty() || val_peek(1).sval.isEmpty())) {
                        ArrayList<String> ambitos = scope.getAmbitos(val_peek(1).sval);
                        String _attributes = ambitos.get(0); 
                        String _class = ambitos.get(2);

                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
                        TablaSimbolos.addTipoVariable(val_peek(2).sval, val_peek(1).sval);
                        
                        if (TablaSimbolos.isClass(val_peek(2).sval + Scope.getScopeMain())) {
                            TablaSimbolos.addUsoInstancia(val_peek(1).sval);
                            TablaClases.addAtributos(val_peek(2).sval, _attributes, _class);
                        }
                        else 
                            TablaClases.addAtributos(_attributes, _class);
                      }

           
                   }
break;
case 21:
//#line 135 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 23:
//#line 139 "grammar.y"
{ yyval = new ParserVal(val_peek(2).sval + ";" + val_peek(0).sval );}
break;
case 25:
//#line 143 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 26:
//#line 144 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 27:
//#line 145 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 28:
//#line 146 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 29:
//#line 147 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 30:
//#line 148 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 31:
//#line 151 "grammar.y"
{
                          if (!scope.isDeclaredInMyScope(val_peek(0).sval)) {
                              yyval = new ParserVal(scope.changeScope(val_peek(0).sval));
                          }
                          else {
                              Logger.logError(aLexico.getProgramPosition(), "La variable " + val_peek(0).sval + " ya esta declarado en el ambito " + scope.getCurrentScope() + ".");
                              yyval = new ParserVal("");
                          }
                        }
break;
case 33:
//#line 165 "grammar.y"
{
                      if (!val_peek(2).sval.isEmpty()) {
                        ArrayList<String> ambitos = scope.getAmbitos(val_peek(2).sval);
                        if (ambitos.size() > 2) {
                          String _method = ambitos.get(0); 
                          String _class = ambitos.get(2);

                            if (val_peek(1).sval.isEmpty()) {
                              TablaClases.addMetodoIMPL(_method, _class);
                              TablaSimbolos.setFuncPrototype(val_peek(2).sval);
                            } else {
                              TablaClases.addMetodo(_method, _class);
                              TablaSimbolos.setImplemented(val_peek(2).sval);
                            }
                        } else { 
                            Logger.logError(aLexico.getProgramPosition(), "Hay un error en la declaracion del metodo.");
                        }
                      scope.deleteLastScope();
                      }
                   }
break;
case 34:
//#line 185 "grammar.y"
{
                      if (!val_peek(1).sval.isEmpty()) {
                        ArrayList<String> ambitos = scope.getAmbitos(val_peek(1).sval);
                        
                        if (ambitos.size() > 2) {
                          String _method = ambitos.get(0); 
                          String _class = ambitos.get(2);

                            if (val_peek(0).sval.isEmpty()) {
                              TablaClases.addMetodoIMPL(_method, _class);
                              TablaSimbolos.setFuncPrototype(val_peek(1).sval);
                            } else {
                              TablaClases.addMetodo(_method, _class);
                              TablaSimbolos.setImplemented(val_peek(1).sval);
                            }
                        } else { 
                            Logger.logError(aLexico.getProgramPosition(), "Hay un error en la declaracion del metodo.");
                        }
                      scope.deleteLastScope();
                      }
                   }
break;
case 35:
//#line 208 "grammar.y"
{yyval = val_peek(0);}
break;
case 36:
//#line 209 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite retornar un tipo, el retorno debe ser VOID."); yyval = new ParserVal("");}
break;
case 38:
//#line 215 "grammar.y"
{
                      String ref = val_peek(3).sval;
                      String par = val_peek(1).sval;
                      if (!ref.isEmpty() && !par.isEmpty()) {
                        yyval = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref, par);
                      } else {
                        yyval = new ParserVal("");
                      }
                      
                  }
break;
case 39:
//#line 226 "grammar.y"
{
                      String ref = val_peek(2).sval;
                      if (!ref.isEmpty()) {
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo sin p/j de parametro.");
                        yyval = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref);
                      } else {
                        yyval = val_peek(2);
                      } 
                      
                  }
break;
case 40:
//#line 237 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 41:
//#line 238 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
break;
case 42:
//#line 241 "grammar.y"
{
                if(!scope.isDeclaredInMyScope(val_peek(0).sval)) {
                  yyval = new ParserVal(val_peek(0).sval);
                  if (scope.hasPassedNesting()) {
                    Logger.logError(aLexico.getProgramPosition(), "Solo se permite 1 nivel de anidamiento, el metodo/funcion " + val_peek(0).sval + " no cumple con esto.");
                    yyval = new ParserVal("");
                  } else {
                    yyval = new ParserVal(scope.changeScope(val_peek(0).sval));
                    TablaSimbolos.addFunction(yyval.sval);
                    scope.stack(val_peek(0).sval);
                  }
                } else {
                  Logger.logError(aLexico.getProgramPosition(), "El metodo ya esta declarado en el ambito" + scope.getCurrentScope() + ".");
                  yyval = new ParserVal("");
                  }                 
              }
break;
case 44:
//#line 261 "grammar.y"
{yyval = new ParserVal("");}
break;
case 45:
//#line 266 "grammar.y"
{
                      if (TablaSimbolos.isClass(val_peek(1).sval + Scope.getScopeMain())) 
                        Logger.logError(aLexico.getProgramPosition(), "No se permite que un parametro real sea del tipo de una clase.");
                      
                      if (!val_peek(0).sval.isEmpty()) {
                          yyval = new ParserVal(val_peek(0).sval);
                          TablaSimbolos.addTipoVariable(val_peek(1).sval, val_peek(0).sval);
                      } else yyval = new ParserVal(""); 
                  }
break;
case 47:
//#line 280 "grammar.y"
{
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una herencia compuesta.");

                            if (!val_peek(1).sval.isEmpty()) { /*si existe la clase a la cual quiere heredar*/
                                ArrayList<String> ambitos = scope.getAmbitos();
                                String _parentClass = val_peek(1).sval; 
                                String _class = ambitos.get(1);

                                TablaClases.addHerencia(_class, _parentClass);
                                Logger.logRule(aLexico.getProgramPosition(), "La clase " + _class + " hereda de " + _parentClass + ".");
                            } else {
                                Logger.logError(aLexico.getProgramPosition(), "La clase a la cual se quiere heredar no existe");
                            }
                        }
break;
case 48:
//#line 294 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
break;
case 49:
//#line 295 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
break;
case 50:
//#line 298 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); if (val_peek(0).sval.contains(";")) Logger.logError(aLexico.getProgramPosition(), "No se permite implementar multiples interfaces.");}
break;
case 52:
//#line 302 "grammar.y"
{yyval = new ParserVal(val_peek(2).sval + ";" + val_peek(0).sval);}
break;
case 53:
//#line 303 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
break;
case 54:
//#line 306 "grammar.y"
{yyval = val_peek(1);}
break;
case 55:
//#line 309 "grammar.y"
{
                    if (!scope.isDeclaredInMyScope(val_peek(0).sval)) {
                      TablaClases.addInterface(val_peek(0).sval);
                      TablaSimbolos.addInterface(val_peek(0).sval);
                      yyval = new ParserVal(val_peek(0).sval);
                    } else {
                      Logger.logError(aLexico.getProgramPosition(), "La interface " + val_peek(0).sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                      yyval = new ParserVal("");
                    }
                      scope.stack(val_peek(0).sval); 
                }
break;
case 57:
//#line 323 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 59:
//#line 325 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 62:
//#line 332 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite la declaracion de constantes en las interfaces.");}
break;
case 63:
//#line 333 "grammar.y"
{
                                if (!val_peek(0).sval.isEmpty()) {
                                  ArrayList<String> ambitos = scope.getAmbitos(val_peek(0).sval);
                                  String _method = ambitos.get(0);
                                  String _class = ambitos.get(2);

                                  TablaClases.addMetodoIMPL(_method, _class);

                                  scope.deleteLastScope();
                                }
                             }
break;
case 64:
//#line 344 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la herencia en una interface.");}
break;
case 66:
//#line 350 "grammar.y"
{yyval = val_peek(1);}
break;
case 67:
//#line 351 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 68:
//#line 352 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
break;
case 70:
//#line 356 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 71:
//#line 357 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 72:
//#line 358 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 74:
//#line 362 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 76:
//#line 364 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 80:
//#line 375 "grammar.y"
{

                                    if (!val_peek(1).sval.isEmpty()){

                                      ArrayList<String> ambitos = scope.getAmbitos(val_peek(1).sval);
                                      if (ambitos.size() > 2) {
                                        String _class = ambitos.get(1); 
                                        String _method = ambitos.get(2);                                      

                                        if (!TablaClases.esUnMetodoAImplementar(_method, _class)){
                                          if (TablaClases.esUnMetodoConcreto(_method, _class)) {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo ya implementado (IMPL FOR)");
                                          } else {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo que no existe (IMPL FOR)");
                                          }  
                                        } else {
                                          TablaSimbolos.setImplemented(val_peek(1).sval.replaceAll(".*@([^@]*)@([^@]*)@([^@:]*):([^@]*)", "$3@$1@$2"));
                                          TablaClases.setMetodoIMPL(_method, _class);
                                        }
                                      }
                                     }
                                  }
break;
case 81:
//#line 399 "grammar.y"
{yyval = val_peek(0);}
break;
case 82:
//#line 404 "grammar.y"
{
                                String ref = val_peek(3).sval;
                                String par = val_peek(1).sval;

                                if (!ref.isEmpty()) 
                                  yyval = new ParserVal(ref + TablaClases.TYPE_SEPARATOR + par);
                                else 
                                  Logger.logError(aLexico.getProgramPosition(), "No se reconocio el metodo a sobreescribir con p/j de parametro.");
                                
                            }
break;
case 83:
//#line 414 "grammar.y"
{
                                yyval = new ParserVal(val_peek(2).sval + TablaClases.TYPE_SEPARATOR + TablaSimbolos.SIN_PARAMETRO);
                            }
break;
case 84:
//#line 417 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 85:
//#line 418 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
break;
case 86:
//#line 421 "grammar.y"
{yyval = val_peek(1);}
break;
case 87:
//#line 425 "grammar.y"
{
                    scope.stack(val_peek(0).sval);
                    yyval = new ParserVal(scope.getCurrentScope());
                  }
break;
case 89:
//#line 433 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 90:
//#line 441 "grammar.y"
{
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
                tercetos.add("=", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval));
                tercetos.declaredFactorsUsed(val_peek(0).sval);
           }
break;
case 91:
//#line 446 "grammar.y"
{
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion de resta.");
                tercetos.add("=", val_peek(2).sval, tercetos.add("-", val_peek(2).sval, val_peek(0).sval));
                tercetos.declaredFactorsUsed(val_peek(0).sval);
           }
break;
case 92:
//#line 451 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 93:
//#line 452 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 94:
//#line 453 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 95:
//#line 454 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 96:
//#line 455 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 98:
//#line 459 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede invocar un metodo/funcion en el lado izquierdo de una asignación.");}
break;
case 99:
//#line 460 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede utilizar constantes en el lado izquierdo de una asignación.");}
break;
case 100:
//#line 464 "grammar.y"
{yyval = new ParserVal(val_peek(2).sval + "." + val_peek(0).sval);}
break;
case 103:
//#line 471 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 104:
//#line 472 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("==", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 105:
//#line 473 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("!!", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 106:
//#line 476 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 107:
//#line 477 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<", val_peek(2).sval, val_peek(0).sval));}
break;
case 108:
//#line 478 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">", val_peek(2).sval, val_peek(0).sval));}
break;
case 109:
//#line 479 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">=", val_peek(2).sval, val_peek(0).sval));}
break;
case 110:
//#line 480 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<=", val_peek(2).sval, val_peek(0).sval));}
break;
case 111:
//#line 483 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 112:
//#line 486 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 113:
//#line 487 "grammar.y"
{yyval = new ParserVal(tercetos.add("+", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 114:
//#line 488 "grammar.y"
{yyval = new ParserVal(tercetos.add("-", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 115:
//#line 491 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 116:
//#line 492 "grammar.y"
{yyval = new ParserVal(tercetos.add("*", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 117:
//#line 493 "grammar.y"
{yyval = new ParserVal(tercetos.add("/", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 118:
//#line 494 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
break;
case 121:
//#line 499 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede invocar un metodo/funcion en una expresion.");}
break;
case 123:
//#line 501 "grammar.y"
{
                    if (tercetos.hasNestingExpressions(val_peek(1).sval)) 
                      Logger.logError(aLexico.getProgramPosition(), "No se permite el anidamiento de expresiones.");
                    yyval = new ParserVal(val_peek(1).sval);  
                 }
break;
case 124:
//#line 506 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 125:
//#line 509 "grammar.y"
{
                          yyval = new ParserVal(tercetos.add("TOD", val_peek(1).sval, "-"));
                          tercetos.TODtracking(yyval.sval);
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");
                       }
break;
case 126:
//#line 514 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 127:
//#line 515 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 128:
//#line 516 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 129:
//#line 517 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 130:
//#line 520 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); }
break;
case 131:
//#line 521 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 132:
//#line 522 "grammar.y"
{yyval = new ParserVal(TablaTipos.chequearRangoLong(val_peek(0).sval, aLexico.getProgramPosition()));}
break;
case 133:
//#line 523 "grammar.y"
{ yyval = new ParserVal(TablaTipos.negarDouble(val_peek(0).sval));}
break;
case 134:
//#line 524 "grammar.y"
{
              if(!TablaTipos.chequearRangoLongNegativo(val_peek(0).sval)){
                  Logger.logWarning(aLexico.getProgramPosition(),"LONG NEGATIVO FUERA DE RANGO SE TRUNCA AL MINIMO PERMITIDO");
                  yyval = new ParserVal(TablaTipos.negarLong("2147483648"));
              } else{
                  yyval = new ParserVal(TablaTipos.negarLong(val_peek(0).sval));
              }
        }
break;
case 135:
//#line 532 "grammar.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 136:
//#line 535 "grammar.y"
{
                String ref = val_peek(3).sval;

                if ( !ref.isEmpty() ){
                  yyval = new ParserVal(ref);
                  
                  if (!tercetos.linkFunction(ref, val_peek(1).sval))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
break;
case 137:
//#line 547 "grammar.y"
{
                String ref = val_peek(2).sval;

                if (!ref.isEmpty()){
                  yyval = new ParserVal(ref);
                  
                  if (!tercetos.linkFunction(ref))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
break;
case 138:
//#line 559 "grammar.y"
{
                String ref = val_peek(3).sval;

                if ( !ref.isEmpty() ){
                  yyval = new ParserVal(ref);
                  
                  if (!tercetos.linkMethod(ref, val_peek(1).sval, scope.getCurrentScope()))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
break;
case 139:
//#line 571 "grammar.y"
{
                String ref = val_peek(2).sval;

                if (!ref.isEmpty()){
                  yyval = new ParserVal(ref);
                  
                  if (!tercetos.linkMethod(ref, scope.getCurrentScope()))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
break;
case 142:
//#line 585 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 143:
//#line 586 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 149:
//#line 605 "grammar.y"
{yyval = new ParserVal("UINT");}
break;
case 150:
//#line 606 "grammar.y"
{yyval = new ParserVal("LONG");}
break;
case 151:
//#line 609 "grammar.y"
{yyval = new ParserVal("DOUBLE");}
break;
case 152:
//#line 613 "grammar.y"
{
                        String reference = scope.searchInterface(val_peek(0).sval);
                        if (reference == null) {
                          yyval = new ParserVal("");
                          Logger.logError(aLexico.getProgramPosition(), "La interface " + val_peek(0).sval + " no esta al alcance.");
                        } else {
                          yyval = new ParserVal(val_peek(0).sval);
                        }
                    }
break;
case 153:
//#line 624 "grammar.y"
{
              String reference = scope.searchClass(val_peek(0).sval);

              if (reference == null) {
                Logger.logError(aLexico.getProgramPosition(), "La clase " + val_peek(0).sval + " no esta al alcance.");
                yyval = new ParserVal("");
              } else {
                yyval = new ParserVal(val_peek(0).sval);
              }
            }
break;
case 154:
//#line 636 "grammar.y"
{
                    String reference = scope.searchClass(val_peek(0).sval);
                    /*Revisar esto*/
                    if (reference == null) {
                      Logger.logError(aLexico.getProgramPosition(), "La clase " + val_peek(0).sval + " no esta al alcance.");
                      yyval = new ParserVal("");
                    } else {
                      scope.stack(val_peek(0).sval);
                      yyval = new ParserVal(reference);
                    }
                  }
break;
case 155:
//#line 650 "grammar.y"
{
                      String reference = scope.searchFunc(val_peek(0).sval);

                      if(reference == null) {
                        Logger.logError(aLexico.getProgramPosition(), "La funcion " + val_peek(0).sval + " no esta al alcance.");
                        yyval = new ParserVal("");
                      }
                      else
                        yyval = new ParserVal(reference);
               }
break;
case 156:
//#line 662 "grammar.y"
{
                    String instance = TablaClases.getInstance(val_peek(0).sval);
                    instance = scope.searchInstance(instance);

                    if (instance != null) {
                      String reference = TablaClases.searchMethod(val_peek(0).sval, scope.getAmbito(instance));

                      if(reference == null) {
                        Logger.logError(aLexico.getProgramPosition(), "El metodo " + val_peek(0).sval + " no esta al alcance.");
                        yyval = new ParserVal("");
                      } else
                        yyval = new ParserVal(reference);
                    } else {
                        Logger.logError(aLexico.getProgramPosition(), "La instancia " + instance + " no esta al alcance.");
                        yyval = new ParserVal("");
                    }
               }
break;
case 157:
//#line 682 "grammar.y"
{
                    String reference = scope.searchVar(val_peek(0).sval);
                    if(reference == null) {
                      Logger.logError(aLexico.getProgramPosition(), "La variable " + val_peek(0).sval + " no esta al alcance.");
                      yyval = new ParserVal("");
                    }
                    else
                      yyval = new ParserVal(reference);
               }
break;
case 158:
//#line 698 "grammar.y"
{tercetos.addReturn();}
break;
case 159:
//#line 699 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN.");}
break;
case 160:
//#line 700 "grammar.y"
{tercetos.addReturn();}
break;
case 161:
//#line 701 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 162:
//#line 702 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 163:
//#line 703 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 164:
//#line 704 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 165:
//#line 705 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 166:
//#line 706 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 169:
//#line 711 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 170:
//#line 712 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 171:
//#line 713 "grammar.y"
{tercetos.addReturn();}
break;
case 172:
//#line 714 "grammar.y"
{tercetos.addReturn();}
break;
case 173:
//#line 716 "grammar.y"
{Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 186:
//#line 741 "grammar.y"
{
                              if (!(val_peek(2).sval.isEmpty() || val_peek(1).sval.isEmpty())) {
                                TablaSimbolos.addTipoVariable(val_peek(2).sval, val_peek(1).sval);                      

                                if (TablaSimbolos.isClass(val_peek(2).sval + Scope.getScopeMain())) {
                                  TablaClases.addInstancia(val_peek(2).sval, val_peek(1).sval);
                                  TablaSimbolos.addUsoInstancia(val_peek(1).sval);
                                }
                                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");
                              }
                            }
break;
case 197:
//#line 770 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 201:
//#line 781 "grammar.y"
{
                         tercetos.backPatching(0);
                         tercetos.addLabel();
                         Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 202:
//#line 785 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con la palabra reservada END_IF.");}
break;
case 203:
//#line 788 "grammar.y"
{tercetos.addCondBranch(val_peek(1).sval);}
break;
case 204:
//#line 789 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 205:
//#line 790 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
break;
case 209:
//#line 796 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 210:
//#line 799 "grammar.y"
{
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
                            tercetos.backPatching(0);
                            tercetos.addLabel();
                          }
break;
case 211:
//#line 806 "grammar.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 212:
//#line 807 "grammar.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 213:
//#line 808 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 216:
//#line 813 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
break;
case 218:
//#line 819 "grammar.y"
{
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
                          tercetos.add("+", val_peek(4).sval, "-");
                          tercetos.backPatching();
                          tercetos.stack();
                          tercetos.add("=", val_peek(4).sval, yyval.sval);
                          tercetos.backPatching();
                          tercetos.addUncondBranch(false);
                          tercetos.backPatching(0); /*Agrego el salto del CB*/
                          tercetos.backPatching(); /*Agrego el salgo del UB*/
                          tercetos.addLabel();
                         }
break;
case 219:
//#line 833 "grammar.y"
{
                               if (!val_peek(0).sval.isEmpty()) {
                                yyval = new ParserVal(val_peek(0).sval);
                                tercetos.add("=", val_peek(0).sval, "-");
                                tercetos.stack(val_peek(0).sval);
                                tercetos.stack();
                               }
                         }
break;
case 220:
//#line 841 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Error en la signatura del FOR IN RANGE.");}
break;
case 221:
//#line 844 "grammar.y"
{
                      String msj = TablaTipos.checkTypeCondition(val_peek(5).sval, val_peek(3).sval, val_peek(1).sval);

                      if (!msj.isEmpty()) {
                        Logger.logError(aLexico.getProgramPosition(), msj);
                      } else {
                        tercetos.backPatching(val_peek(5).sval);
                        String ref = tercetos.addLabel();
                        yyval = new ParserVal(tercetos.add(tercetos.getComparator(val_peek(1).sval), val_peek(3).sval, "-"));
                        tercetos.backPatching();
                        tercetos.stack(ref);
                        tercetos.addCondBranch(yyval.sval);
                        tercetos.stack("+" + val_peek(1).sval);
                      }
                  }
break;
case 222:
//#line 859 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 225:
//#line 866 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 226:
//#line 869 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 227:
//#line 872 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 228:
//#line 875 "grammar.y"
{
                        scope.deleteLastScope();
                      }
break;
case 230:
//#line 881 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la funcion.");}
break;
case 231:
//#line 884 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT."); tercetos.add("PRINT", val_peek(1).sval, "-");}
break;
case 232:
//#line 885 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena en la sentencia PRINT.");}
break;
case 233:
//#line 886 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 234:
//#line 887 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 2197 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
