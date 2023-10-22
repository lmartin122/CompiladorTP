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






//#line 2 "grammer.y"
package Sintactico;

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

//#line 35 "Parser.java"




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
    7,    8,    8,   10,   10,   11,   12,   12,   13,   13,
   16,   16,   17,   17,   17,   17,   17,   17,   17,   18,
   19,   14,   21,   23,   24,   24,   24,   24,   24,   25,
   22,   22,   26,   28,    9,   29,   29,   29,    4,   31,
   31,   31,   31,   32,   32,   33,   33,   34,   35,   35,
   35,    5,    5,    5,    5,    5,   37,   37,   38,   38,
   39,   40,   41,   41,   42,   43,   43,   45,   46,   46,
   47,   47,   47,   48,   48,   48,   48,   48,   20,   20,
   20,   20,   20,   20,   20,   49,   49,   49,   50,   50,
   50,   50,   51,   51,   51,   51,   52,   52,   52,   52,
   52,   52,   44,   44,   44,   44,   44,   44,   44,   53,
   53,   53,   53,   53,   53,   15,   15,   54,   36,   55,
   55,   56,   56,   57,   30,   27,   27,   27,   27,   27,
   27,   27,   59,   59,   58,   58,   60,   60,    6,    6,
   61,   61,   61,   61,   61,   61,   62,   70,   63,   63,
   63,   63,   63,   63,   71,   71,   71,   68,   68,   72,
   72,   69,   64,   64,   73,   73,   73,   73,   74,   74,
   74,   74,   74,   75,   75,   75,   75,   75,   65,   65,
   76,   66,   66,   66,   66,   66,   66,   66,   66,   66,
   66,   66,   77,   78,   80,   79,   67,   67,   67,   67,
   67,
};
final static short yylen[] = {                            2,
    3,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    1,    3,    3,    1,    2,    1,    1,    1,    3,    2,
    1,    3,    1,    3,    4,    3,    3,    3,    3,    1,
    1,    2,    2,    1,    4,    4,    5,    3,    3,    1,
    1,    1,    2,    1,    2,    1,    3,    3,    3,    3,
    3,    2,    2,    1,    2,    1,    1,    2,    3,    2,
    3,    5,    6,    6,    6,    4,    3,    3,    1,    2,
    1,    2,    1,    1,    3,    1,    1,    3,    1,    1,
    1,    3,    3,    1,    3,    3,    3,    3,    1,    4,
    4,    4,    3,    3,    1,    1,    3,    3,    1,    3,
    3,    3,    1,    3,    2,    1,    1,    1,    1,    2,
    2,    2,    1,    1,    2,    1,    1,    1,    1,    4,
    3,    5,    4,    3,    5,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    5,    3,    5,    4,    4,
    2,    2,    3,    2,    1,    2,    1,    2,    1,    1,
    1,    1,    1,    1,    1,    1,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    2,    2,    1,
    1,    1,    5,    4,    3,    3,    3,    2,    1,    1,
    1,    2,    2,    1,    1,    1,    2,    2,    5,    4,
    3,   12,   12,   12,   12,   13,    8,    8,   11,   11,
   11,   11,    1,    1,    1,    1,    3,    3,    3,    3,
    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   34,  133,  132,  134,    0,    0,    0,  172,    0,    3,
    5,    6,    7,    8,  163,    0,    0,    0,  165,    0,
  170,    0,    0,    0,  171,  126,  128,  130,  131,  149,
  150,  160,  161,  162,  164,  167,  166,    0,  159,    0,
    0,    0,  203,    0,   11,    0,    0,    0,    0,    0,
  129,    0,    0,    0,    0,  211,    0,    0,    0,  141,
  145,    0,    0,  142,    0,    1,    4,   30,    0,   21,
    0,   42,   32,   41,   40,   33,    0,    0,  116,  117,
  119,  118,  114,  113,    0,    0,    0,  157,  168,  169,
  210,    0,    0,    0,    0,    0,    9,    0,    0,    0,
   49,  106,  107,  108,  109,    0,    0,    0,    0,    0,
    0,   99,  103,    0,  178,    0,    0,    0,  180,  179,
  181,  151,  152,  153,  154,  155,  156,    0,    0,    0,
    0,    0,  209,  208,  207,   95,    0,  121,   44,    0,
    0,    0,    0,  137,  146,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  115,   75,  124,    0,
   78,    0,    0,   66,    0,  135,    0,   46,    0,   14,
   16,   17,   18,    0,  127,    0,   10,   52,    0,    0,
    0,   54,   56,   57,   53,    0,  110,  112,  111,  105,
    0,    0,    0,  177,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  176,  175,  182,  183,  144,    0,    0,
  147,    0,    0,  174,    0,  190,    0,    0,    0,    0,
    0,    0,    0,  120,  139,    0,  140,    0,   22,    0,
   26,   31,   27,   29,   28,   24,   39,    0,    0,   38,
    0,    0,  123,    0,    0,   69,   71,    0,  204,    0,
    0,    0,   12,   15,    0,   13,    0,    0,   50,   55,
   51,  104,    0,    0,    0,    0,    0,    0,    0,    0,
  100,  101,  102,  143,  148,    0,  185,  184,  186,  191,
  173,  189,    0,    0,    0,    0,   62,    0,   93,    0,
   94,    0,  122,  136,  138,   25,   43,   36,    0,   35,
  125,   74,   73,   72,   67,   70,   68,    0,    0,   48,
   47,   19,   59,   61,  187,  188,    0,    0,   64,   65,
   63,   92,   91,   90,   37,  206,    0,    0,    0,    0,
    0,    0,    0,  197,  198,    0,    0,  205,    0,    0,
    0,    0,    0,    0,    0,    0,  201,  202,  199,  200,
    0,    0,  194,  195,    0,  192,  193,  196,
};
final static short yydgoto[] = {                          3,
   19,   20,   21,   22,   23,   24,   56,  107,  108,  179,
  180,  181,  182,   25,   26,   79,   80,   81,  241,  242,
   27,   83,   28,   86,   87,  249,   29,  150,  177,  178,
  111,  191,  192,  193,  194,  185,  174,  255,  256,  257,
  314,   31,   32,   95,   33,   34,  118,  119,  151,  121,
  122,  123,   35,   36,   37,   38,   39,   72,  129,  220,
  130,   40,   41,  132,  133,  134,  135,  136,  137,   48,
   49,   50,   60,  138,  290,  139,   54,  260,  337,  349,
};
final static short yysindex[] = {                       -99,
    0,   63,    0, -202, -216, -192,   -3, -167, -145,    1,
    0,    0,    0,    0,  111,   89,  112,    0,   35,    0,
    0,    0,    0,    0,    0, -117,   -5, -111,    0,    0,
    0,  485,  140,  151,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  155,    0,   52,
  165,  172,    0,  -43,    0,  -31,    9,  -11,  -15,  279,
    0,   61, -224,  274,  -23,    0,  -37,  -77,  300,    0,
    0,  154,  317,    0,  160,    0,    0,    0,  312,    0,
  491,    0,    0,    0,    0,    0,   11,  316,    0,    0,
    0,    0,    0,    0,   -7,  -29,   98,    0,    0,    0,
    0,   17,  357,  125,  342,  342,    0,   20,  -70,  209,
    0,    0,    0,    0,    0, -104,  -22,  -97,  296,  103,
  134,    0,    0,  372,    0,   58,   18,  341,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   70,   -8,  153,
  381,  -45,    0,    0,    0,    0,   31,    0,    0,  -14,
  103,  320,  403,    0,    0,  414,  424, -117,  400,   -7,
   -7,   -7,   -7,   -7,  -85,   28,    0,    0,    0,   26,
    0,  196,  196,    0,   65,    0,   56,    0,  318,    0,
    0,    0,    0, -117,    0,  223,    0,    0, -117, -111,
  369,    0,    0,    0,    0,  237,    0,    0,    0,    0,
  438,  -11,  -11,    0,  -11,  -11,  -11,  -11,  -11,  -11,
  -11,  -11,  -11,    0,    0,    0,    0,    0,    0,  368,
    0,  310,  436,    0,  437,    0,  451,   17,  434,  -30,
  -94,    7,  441,    0,    0,  371,    0,  469,    0,   -7,
    0,    0,    0,    0,    0,    0,    0, -117,  380,    0,
   86,  475,    0,   19, -105,    0,    0,  -36,    0,   62,
  125,  125,    0,    0,   72,    0,  312,   78,    0,    0,
    0,    0,  296,  296,  103,  103,  103,  103,  134,  134,
    0,    0,    0,    0,    0,   37,    0,    0,    0,    0,
    0,    0,   53,  481,   17,  487,    0,  394,    0,  493,
    0,  483,    0,    0,    0,    0,    0,    0,  495,    0,
    0,    0,    0,    0,    0,    0,    0,   65,   65,    0,
    0,    0,    0,    0,    0,    0,  497,  100,    0,    0,
    0,    0,    0,    0,    0,    0,  488,  489,  281,   65,
   65,   65,   65,    0,    0,  500,  492,    0,  509,  513,
   65,   65,  281,  281,  517,  521,    0,    0,    0,    0,
  281,  347,    0,    0,    6,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  309,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  417,
    0,    0,  460,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  307,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  520,    0,
  131,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -34,  428,
  123,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  106,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   32,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  472,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  176,    0,  182,  197,    0,    0,
    0,    0,   24,  104,  435,  442,  467,  479,  382,  405,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  551,    0,    0,    0,   29,    0,  465,    0,  466,
  -74,    0,    0,  -62,  613,  -87,  418,  327,  -79,   13,
 -119,    0,  -68,  387,    0,  419,   60,  503,    0,  -46,
    0,  474,    5,    0,    0,   -2, -154,  427, -120,    0,
    0,    0,    0,    0,    0,    0,  532,   22,  464,   12,
  -24,  455,    0,    0,    0,    0,    0,  584,  415,    0,
  289,  -38,    0,  708,  765,  769,  798,  803,  808,    0,
    0,    0,    0,    0,    0,    0,  598,  314, -251, -257,
};
final static int YYTABLESIZE=883;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         30,
   66,   53,  117,  148,  317,   53,   81,  116,  106,  173,
  117,  169,  230,   30,   30,  116,   30,  117,  200,  315,
  145,  131,  116,    2,  117,  125,  234,  204,  117,  116,
  299,  141,  117,  116,   17,  226,   59,  116,   82,  247,
  190,  190,  183,  183,   71,   71,  117,  301,  110,  368,
  166,  116,  254,  254,  188,   61,  173,   30,   17,  106,
  142,  217,  312,   55,   82,   53,  253,  338,  250,   30,
  232,   45,   30,  294,   17,  297,   51,   52,   18,  149,
  326,  243,  244,  245,  246,  350,   84,   57,  346,  347,
   81,  105,  172,  355,  356,   99,  265,  116,  215,  261,
  155,  267,   17,  155,  264,  318,   18,  168,  149,  116,
  100,  264,   61,  224,  262,  322,  183,   16,   63,   58,
  319,  323,  190,  183,   53,  219,  310,  190,   17,  201,
  158,  109,   18,  165,  316,  254,  324,  316,  254,  172,
  330,   16,  105,  340,   83,  209,   89,  210,   82,   89,
   67,   17,   74,  231,   45,   18,    1,   16,  341,   76,
  306,  298,   78,   96,   89,   96,   96,   96,   85,   11,
  213,   23,  202,  203,   23,  211,  197,  198,  199,   96,
  212,   96,   96,  289,   96,   16,  281,  282,  283,   23,
   12,   13,   14,   17,   61,  270,   97,   18,   98,   17,
  270,   51,   61,   18,   11,   12,   13,   14,  101,   61,
  229,   16,  103,   70,  320,  321,   20,  219,  146,   30,
  279,  280,   58,  273,  274,  296,  146,  104,   83,  102,
   89,  147,  144,  146,   16,   81,   81,   60,   11,  147,
  124,  233,  112,  113,  114,  115,  147,   96,  146,  195,
  112,  113,  114,  115,  225,   23,   64,  112,  113,  114,
  115,  147,  300,  266,  112,  113,  114,  115,  112,  113,
  114,  115,  112,  113,  114,  115,   16,  271,  154,   65,
  216,  252,   16,   53,   51,   61,  112,  113,  114,  115,
    4,    5,    6,   82,   82,    7,   51,   61,    8,  325,
   20,    9,   10,   12,   13,   14,   58,   61,  327,   11,
   12,   13,   14,  313,   15,   51,   61,  143,    4,    5,
    6,   60,   18,    7,   18,  140,    8,  202,  203,    9,
   10,  222,  223,  113,  114,  115,  219,   11,   12,   13,
   14,  309,   15,  152,   68,  113,  114,  115,   69,    7,
  219,  219,    8,   18,  129,  207,   10,  208,  219,  219,
  156,   89,   53,   11,   12,   13,   14,   68,   15,  129,
  158,   73,    7,   83,   83,    8,  167,  171,   96,   10,
   89,   89,   89,   89,   18,   89,   11,   12,   13,   14,
   18,   15,   96,   96,   96,   96,  175,   96,   96,   96,
   96,  128,   96,  128,  176,   23,   23,   23,   23,   68,
   23,   18,  214,  153,    7,   68,  221,    8,  227,  157,
    7,   10,   97,    8,   97,   97,   97,   10,   11,   12,
   13,   14,  128,   15,   11,   12,   13,   14,  228,   15,
   97,   97,  263,   97,  235,   98,  236,   98,   98,   98,
   20,   20,   20,   20,  237,   20,   58,   58,   58,   58,
  240,   58,   79,   98,   98,  218,   98,  238,   84,  128,
   11,   60,   60,   60,   60,   87,   60,   76,  272,  291,
  292,  303,   88,   11,   12,   13,   14,   84,   61,   84,
  293,  295,  284,  269,   87,  304,   87,   11,   12,   13,
   14,   88,   61,   88,  308,   80,   97,   85,  285,  305,
  288,   11,   12,   13,   14,  311,   61,   79,  332,   86,
   77,  120,  120,  334,  329,  209,   85,  210,   85,   98,
  331,  342,   76,  333,  127,  335,   68,  339,   86,    7,
   86,    7,    8,  351,    8,   94,   10,  343,   10,  353,
  352,  164,   84,  354,   12,   13,   14,  361,   15,   87,
   15,  362,  129,  158,  129,  286,   88,  205,  206,   77,
    7,  186,  187,    8,  307,  239,  268,   10,  129,  129,
  129,  129,  129,  196,  251,   12,   13,   14,  129,   15,
  126,   85,   11,   12,   13,   14,   68,   61,  170,  258,
   75,    7,  365,   86,    8,   62,  328,    7,   10,    0,
    8,    0,    0,    0,   10,    0,   11,   12,   13,   14,
   15,   61,    0,   68,    0,    0,   15,  345,    7,  259,
    0,    8,    0,    0,    0,   10,  287,   97,    0,    0,
    0,  358,  360,   11,   12,   13,   14,   15,   61,  364,
  367,   97,   97,   97,   97,    0,   97,   97,   97,   97,
   98,   97,    0,    0,    0,  120,  120,    0,  275,  276,
  277,  278,   76,    0,   98,   98,   98,   98,    0,   98,
   98,   98,   98,    0,   98,    0,   76,   76,   76,   76,
   76,    0,    0,    0,    0,  302,  127,   84,   84,   84,
   84,    0,    0,    0,   87,   87,   87,   87,    0,   42,
    0,   88,   88,   88,   88,   77,    0,  184,  184,    0,
    0,  189,  189,   42,   42,    0,   42,   76,    0,   77,
   77,   77,   77,   77,    0,    0,   85,   85,   85,   85,
   88,   76,   76,   76,   76,   76,  159,  259,   86,   86,
   86,   86,    0,  344,   89,   90,   91,   92,   93,    0,
  160,  161,  162,  163,    0,    0,   43,  357,  359,    0,
   44,    0,  336,  336,    0,  363,  366,  248,  248,   42,
   43,   43,   42,   43,   44,   44,    0,   44,    0,    0,
    0,  184,    0,    0,  336,  336,  348,  348,  184,   45,
    0,    0,    0,  189,   46,  348,  348,    0,  189,   47,
    0,    0,    0,   45,   45,    0,   45,    0,   46,   46,
    0,   46,    0,   47,   47,    0,   47,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   43,    0,    0,   43,
   44,    0,    0,   44,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   45,
    0,    0,   45,    0,   46,    0,    0,   46,    0,   47,
    0,    0,   47,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          2,
    0,    4,   40,   41,   41,    8,   41,   45,   40,   40,
   40,   41,   58,   16,   17,   45,   19,   40,   41,  125,
   44,   60,   45,  123,   40,   41,   41,  125,   40,   45,
  125,  256,   40,   45,   40,   44,   40,   45,   44,  125,
  109,  110,  105,  106,   16,   17,   40,   41,   40,   44,
   40,   45,  172,  173,  125,  280,   40,   60,   40,   40,
   63,   44,   44,  280,   41,   68,   41,  319,   41,   72,
   40,   40,   75,  228,   40,  230,  279,  280,   44,   67,
   44,  161,  162,  163,  164,  343,   27,  280,  340,  341,
  125,  123,  123,  351,  352,   44,  184,   45,   41,   44,
   72,  189,   40,   75,  179,   44,   44,   95,   96,   45,
   59,  186,  280,   44,   59,   44,  179,  123,  264,  123,
   59,   44,  191,  186,  127,  128,   41,  196,   40,  117,
   59,  123,   44,  123,  255,  255,   59,  258,  258,  123,
  295,  123,  123,   44,   41,   43,   41,   45,  125,   44,
   40,   40,   41,  123,  123,   44,  256,  123,   59,  125,
  240,  256,  280,   41,   59,   43,   44,   45,  280,  275,
   37,   41,  270,  271,   44,   42,  281,  282,  283,   40,
   47,   59,   60,  222,   62,  123,  211,  212,  213,   59,
  276,  277,  278,   40,  280,  191,   46,   44,   44,   40,
  196,  279,  280,   44,  275,  276,  277,  278,   44,  280,
  256,  123,  256,  125,  261,  262,   41,  220,  256,  222,
  209,  210,   41,  202,  203,  256,  256,  259,  125,   58,
  125,  269,  256,  256,  123,  270,  271,   41,  275,  269,
  256,  256,  280,  281,  282,  283,  269,  125,  256,   41,
  280,  281,  282,  283,  263,  125,  256,  280,  281,  282,
  283,  269,  256,   41,  280,  281,  282,  283,  280,  281,
  282,  283,  280,  281,  282,  283,  123,   41,  125,  279,
  263,  256,  123,  286,  279,  280,  280,  281,  282,  283,
  256,  257,  258,  270,  271,  261,  279,  280,  264,  263,
  125,  267,  268,  276,  277,  278,  125,  280,  256,  275,
  276,  277,  278,  254,  280,  279,  280,   44,  256,  257,
  258,  125,   44,  261,   44,  265,  264,  270,  271,  267,
  268,  262,  263,  281,  282,  283,  339,  275,  276,  277,
  278,  256,  280,   44,  256,  281,  282,  283,  260,  261,
  353,  354,  264,   44,   46,   60,  268,   62,  361,  362,
   44,  256,  365,  275,  276,  277,  278,  256,  280,   61,
   59,  260,  261,  270,  271,  264,   61,  280,  256,  268,
  275,  276,  277,  278,   44,  280,  275,  276,  277,  278,
   44,  280,  270,  271,  272,  273,   40,  275,  276,  277,
  278,  123,  280,  123,  280,  275,  276,  277,  278,  256,
  280,   44,   41,  260,  261,  256,  128,  264,  266,  260,
  261,  268,   41,  264,   43,   44,   45,  268,  275,  276,
  277,  278,  123,  280,  275,  276,  277,  278,   58,  280,
   59,   60,  125,   62,  125,   41,   44,   43,   44,   45,
  275,  276,  277,  278,   41,  280,  275,  276,  277,  278,
   61,  280,   46,   59,   60,  125,   62,   44,   41,  123,
  275,  275,  276,  277,  278,   41,  280,   61,   41,   44,
   44,   41,   41,  275,  276,  277,  278,   60,  280,   62,
   40,   58,  125,  125,   60,  125,   62,  275,  276,  277,
  278,   60,  280,   62,  125,   46,  125,   41,  220,   41,
  222,  275,  276,  277,  278,   41,  280,   46,  125,   41,
   61,   58,   59,   41,   44,   43,   60,   45,   62,  125,
   44,   44,   61,   41,  256,   41,  256,   41,   60,  261,
   62,  261,  264,   44,  264,   61,  268,   59,  268,   41,
   59,   61,  125,   41,  276,  277,  278,   41,  280,  125,
  280,   41,  256,   44,  256,  256,  125,  272,  273,   19,
  261,  106,  108,  264,  248,  158,  190,  268,  270,  271,
  272,  273,  274,  110,  166,  276,  277,  278,  280,  280,
   59,  125,  275,  276,  277,  278,  256,  280,   96,  173,
   17,  261,  256,  125,  264,    8,  293,  261,  268,   -1,
  264,   -1,   -1,   -1,  268,   -1,  275,  276,  277,  278,
  280,  280,   -1,  256,   -1,   -1,  280,  339,  261,  175,
   -1,  264,   -1,   -1,   -1,  268,  222,  256,   -1,   -1,
   -1,  353,  354,  275,  276,  277,  278,  280,  280,  361,
  362,  270,  271,  272,  273,   -1,  275,  276,  277,  278,
  256,  280,   -1,   -1,   -1,  202,  203,   -1,  205,  206,
  207,  208,  256,   -1,  270,  271,  272,  273,   -1,  275,
  276,  277,  278,   -1,  280,   -1,  270,  271,  272,  273,
  274,   -1,   -1,   -1,   -1,  232,  280,  270,  271,  272,
  273,   -1,   -1,   -1,  270,  271,  272,  273,   -1,    2,
   -1,  270,  271,  272,  273,  256,   -1,  105,  106,   -1,
   -1,  109,  110,   16,   17,   -1,   19,  256,   -1,  270,
  271,  272,  273,  274,   -1,   -1,  270,  271,  272,  273,
  256,  270,  271,  272,  273,  274,  256,  293,  270,  271,
  272,  273,   -1,  339,  270,  271,  272,  273,  274,   -1,
  270,  271,  272,  273,   -1,   -1,    2,  353,  354,   -1,
    2,   -1,  318,  319,   -1,  361,  362,  165,  166,   72,
   16,   17,   75,   19,   16,   17,   -1,   19,   -1,   -1,
   -1,  179,   -1,   -1,  340,  341,  342,  343,  186,    2,
   -1,   -1,   -1,  191,    2,  351,  352,   -1,  196,    2,
   -1,   -1,   -1,   16,   17,   -1,   19,   -1,   16,   17,
   -1,   19,   -1,   16,   17,   -1,   19,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   72,   -1,   -1,   75,
   72,   -1,   -1,   75,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   72,
   -1,   -1,   75,   -1,   72,   -1,   -1,   75,   -1,   72,
   -1,   -1,   75,
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
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"result_type : VOID",
"method_declarator : method_name '(' formal_parameter ')'",
"method_declarator : method_name '{' formal_parameter '}'",
"method_declarator : method_name '(' formal_parameter error ')'",
"method_declarator : method_name '(' ')'",
"method_declarator : method_name '{' '}'",
"method_name : ID",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : arithmetic_operation",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : type_name",
"interface_type_list : interface_type_list ';' type_name",
"interface_type_list : interface_type_list ',' type_name",
"interface_declaration : INTERFACE ID interface_body",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '(' interface_member_declaration ')'",
"interface_body : '{' '}'",
"interface_body : '(' ')'",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : constant_declaration",
"interface_method_declaration : abstract_method_declaration",
"constant_declaration : type variable_declarators",
"abstract_method_declaration : result_type method_declarator ','",
"abstract_method_declaration : result_type method_declarator",
"abstract_method_declaration : result_type method_declarator ';'",
"implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_type ':' error ','",
"implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
"implement_for_declaration : IMPL FOR reference_type error ':' implement_for_body",
"implement_for_declaration : error ID ':' implement_for_body",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body : '(' implement_for_body_declarations ')'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
"implement_for_method_body : block",
"implement_for_method_body : ','",
"assignment : left_hand_side assignment_operator arithmetic_operation",
"left_hand_side : reference_type",
"left_hand_side : field_acces",
"field_acces : primary '.' ID",
"primary : reference_type",
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
"arithmetic_operation : TOD '(' additive_expression ')'",
"arithmetic_operation : TOD '(' error ')'",
"arithmetic_operation : TOD '{' error '}'",
"arithmetic_operation : TOD '{' '}'",
"arithmetic_operation : TOD '(' ')'",
"arithmetic_operation : error",
"additive_expression : multiplicative_expression",
"additive_expression : additive_expression '+' multiplicative_expression",
"additive_expression : additive_expression '-' multiplicative_expression",
"multiplicative_expression : unary_expression",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : factor",
"unary_expression : '(' arithmetic_operation ')'",
"unary_expression : '(' ')'",
"unary_expression : ID",
"factor : CTE_DOUBLE",
"factor : CTE_UINT",
"factor : CTE_LONG",
"factor : '-' CTE_DOUBLE",
"factor : '-' CTE_LONG",
"factor : '-' CTE_UINT",
"assignment_operator : '='",
"assignment_operator : MINUS_ASSIGN",
"assignment_operator : error '='",
"assignment_operator : EQUAL_OPERATOR",
"assignment_operator : NOT_EQUAL_OPERATOR",
"assignment_operator : LESS_THAN_OR_EQUAL_OPERATOR",
"assignment_operator : GREATER_THAN_OR_EQUAL_OPERATOR",
"method_invocation : ID '(' real_parameter ')'",
"method_invocation : ID '(' ')'",
"method_invocation : ID '(' real_parameter error ')'",
"method_invocation : field_acces '(' real_parameter ')'",
"method_invocation : field_acces '(' ')'",
"method_invocation : field_acces '(' real_parameter error ')'",
"type : primitive_type",
"type : reference_type",
"primitive_type : numeric_type",
"reference_type : ID",
"numeric_type : integral_type",
"numeric_type : floating_type",
"integral_type : UINT",
"integral_type : LONG",
"floating_type : DOUBLE",
"type_name : ID",
"block : '{' block_statements RETURN ',' '}'",
"block : '{' block_statements '}'",
"block : '(' block_statements RETURN ',' ')'",
"block : '{' RETURN ',' '}'",
"block : '(' RETURN ',' ')'",
"block : '{' '}'",
"block : '(' ')'",
"executable_block : '{' executable_block_statements '}'",
"executable_block : '{' '}'",
"block_statements : block_statement",
"block_statements : block_statements block_statement",
"executable_block_statements : executable_statement",
"executable_block_statements : executable_block_statements executable_statement",
"block_statement : local_variable_declaration_statement",
"block_statement : statement",
"executable_statement : if_then_declaration",
"executable_statement : if_then_else_declaration",
"executable_statement : for_in_range_statement",
"executable_statement : print_statement",
"executable_statement : expression_statement",
"executable_statement : empty_statement",
"local_variable_declaration_statement : local_variable_declaration ','",
"local_variable_declaration : type variable_declarators",
"statement : statement_without_trailing_substatement",
"statement : if_then_declaration",
"statement : if_then_else_declaration",
"statement : for_in_range_statement",
"statement : method_declaration",
"statement : print_statement",
"statement_without_trailing_substatement : block",
"statement_without_trailing_substatement : empty_statement",
"statement_without_trailing_substatement : expression_statement",
"expression_statement : statement_expression ','",
"expression_statement : statement_expression ';'",
"statement_expression : assignment",
"statement_expression : method_invocation",
"empty_statement : ','",
"if_then_declaration : IF if_then_cond if_then_body END_IF ','",
"if_then_declaration : IF if_then_cond if_then_body ','",
"if_then_cond : '(' equality_expression ')'",
"if_then_cond : '(' error ')'",
"if_then_cond : '{' equality_expression '}'",
"if_then_cond : '(' ')'",
"if_then_body : executable_statement",
"if_then_body : executable_block",
"if_then_body : local_variable_declaration_statement",
"if_then_body : error END_IF",
"if_then_body : error ','",
"if_else_body : executable_statement",
"if_else_body : executable_block",
"if_else_body : local_variable_declaration_statement",
"if_else_body : error END_IF",
"if_else_body : error ','",
"if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ','",
"if_then_else_declaration : IF if_then_cond if_then_else_body ','",
"if_then_else_body : if_then_body ELSE if_else_body",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statement",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_statement",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statement",
"for_in_range_statement : error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_block",
"for_in_range_statement : error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_statement",
"for_in_range_statement : error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_block",
"for_in_range_statement : error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_statement",
"for_variable : reference_type",
"for_init : factor",
"for_update : factor",
"for_end : factor",
"print_statement : PRINT CADENA ','",
"print_statement : PRINT CADENA error",
"print_statement : PRINT error ','",
"print_statement : error CADENA ','",
"print_statement : PRINT '\\000'",
};

//#line 447 "grammer.y"

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
// metodos auxiliares a la gramatica
// ###############################################################

private String negarDouble(String lexema) {

    String n_lexema = lexema;

    try {
      n_lexema = String.valueOf(-Double.parseDouble(lexema));
    } catch (Exception ex) {}

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }


private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {
      if (tipo == "D") { // Perdon Luis por hacer un if por tipos
        TablaSimbolos.addDouble(n_lexema);
      } else {
        TablaSimbolos.addLong(n_lexema);
      }
      TablaSimbolos.addContador(n_lexema);
    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }

    TablaSimbolos.decreaseCounter(lexema);
}

private String chequearRangoLong(String lexema) {

    long RDN_MAX = (long) Math.pow(2, 31) - 1;
    long number = 0;

    try {
      number = Long.parseLong(lexema);
    } catch (Exception ex) {}

    if (number > RDN_MAX) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      String n_lexema = String.valueOf(RDN_MAX);
      addTablaSimbolos(lexema, n_lexema, "L");

      return n_lexema;
    }

    return lexema;
}


private String negarLong(String lexema) {
  
    long number = 0;

    try {
        number = -Long.parseLong(lexema);
    } catch (Exception ex) {}

    String n_lexema = String.valueOf(number);

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
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
    System.err.println("No es un directorio válido.");
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

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    Parser aSintactico = new Parser();
    tercetos = new Tercetos();
    scope = new Scope();
    
    aSintactico.run();

    //aSintactico.dump_stacks(yylval_recognition);
    System.out.println(Logger.dumpLog());

    if(!Logger.errorsOcurred()){
      System.out.println("No se producieron errores."); //Para la parte 4, generacion de codigo maquina
    }

    tercetos.printRules();
    System.out.println(aLexico.getProgram());

}


//#line 967 "Parser.java"
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
case 1:
//#line 40 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");}
break;
case 2:
//#line 41 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se reconocio el programa.");}
break;
case 3:
//#line 49 "grammer.y"
{scope.reset();}
break;
case 9:
//#line 59 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
break;
case 10:
//#line 60 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
break;
case 11:
//#line 63 "grammer.y"
{scope.stack(val_peek(0).sval);}
break;
case 13:
//#line 67 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de una clase debe estar delimitado por llaves \"{...}\".");}
break;
case 19:
//#line 81 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
break;
case 20:
//#line 82 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La sentencia debe terminar con ','.");}
break;
case 25:
//#line 92 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las declaraciones de variables se deben hacer con el caracter '='.");}
break;
case 26:
//#line 93 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter == no se permite en una declaracion.");}
break;
case 27:
//#line 94 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter !! no se permite en una declaracion.");}
break;
case 28:
//#line 95 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter <= no se permite en una declaracion.");}
break;
case 29:
//#line 96 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter >= no se permite en una declaracion.");}
break;
case 30:
//#line 99 "grammer.y"
{scope.changeScope(val_peek(0).sval);}
break;
case 35:
//#line 114 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 36:
//#line 115 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 37:
//#line 116 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 38:
//#line 117 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 39:
//#line 118 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 40:
//#line 121 "grammer.y"
{scope.stack(val_peek(0).sval);}
break;
case 48:
//#line 141 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
break;
case 49:
//#line 144 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 51:
//#line 148 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 53:
//#line 150 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 60:
//#line 165 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 61:
//#line 166 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
break;
case 62:
//#line 169 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 63:
//#line 170 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 64:
//#line 171 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 65:
//#line 172 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 66:
//#line 173 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 68:
//#line 177 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 74:
//#line 191 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 75:
//#line 199 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion."); tercetos.add(val_peek(1).sval, val_peek(2).sval, val_peek(0).sval);}
break;
case 81:
//#line 213 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 82:
//#line 214 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("==", val_peek(2).sval, val_peek(0).sval));}
break;
case 83:
//#line 215 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("!!", val_peek(2).sval, val_peek(0).sval));}
break;
case 84:
//#line 218 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 85:
//#line 219 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<", val_peek(2).sval, val_peek(0).sval));}
break;
case 86:
//#line 220 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">", val_peek(2).sval, val_peek(0).sval));}
break;
case 87:
//#line 221 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 88:
//#line 222 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 89:
//#line 225 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 90:
//#line 227 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
break;
case 91:
//#line 228 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 92:
//#line 229 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 93:
//#line 230 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 94:
//#line 231 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 95:
//#line 232 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No es una expresion aritmetica valida.");}
break;
case 96:
//#line 235 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 97:
//#line 236 "grammer.y"
{yyval = new ParserVal(tercetos.add("+", val_peek(2).sval, val_peek(0).sval));}
break;
case 98:
//#line 237 "grammer.y"
{yyval = new ParserVal(tercetos.add("-", val_peek(2).sval, val_peek(0).sval));}
break;
case 99:
//#line 240 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 100:
//#line 241 "grammer.y"
{yyval = new ParserVal(tercetos.add("*", val_peek(2).sval, val_peek(0).sval));}
break;
case 101:
//#line 242 "grammer.y"
{yyval = new ParserVal(tercetos.add("/", val_peek(2).sval, val_peek(0).sval));}
break;
case 102:
//#line 243 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
break;
case 105:
//#line 248 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 109:
//#line 254 "grammer.y"
{yyval = new ParserVal(chequearRangoLong(val_peek(0).sval));}
break;
case 110:
//#line 255 "grammer.y"
{yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 111:
//#line 256 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 112:
//#line 257 "grammer.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 113:
//#line 262 "grammer.y"
{yyval = new ParserVal("=");}
break;
case 114:
//#line 263 "grammer.y"
{yyval = new ParserVal("-=");}
break;
case 115:
//#line 264 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 116:
//#line 265 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 117:
//#line 266 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 118:
//#line 267 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 119:
//#line 268 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 120:
//#line 271 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 121:
//#line 272 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 122:
//#line 273 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 123:
//#line 274 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 124:
//#line 275 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 125:
//#line 276 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 129:
//#line 291 "grammer.y"
{scope.changeScope(val_peek(0).sval);}
break;
case 137:
//#line 315 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 138:
//#line 316 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 140:
//#line 318 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 141:
//#line 319 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 142:
//#line 320 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 157:
//#line 348 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 169:
//#line 369 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 173:
//#line 381 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF."); tercetos.backPatching(0); tercetos.addLabel();}
break;
case 174:
//#line 382 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
break;
case 175:
//#line 385 "grammer.y"
{tercetos.addCondBranch(val_peek(1).sval);}
break;
case 176:
//#line 386 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 177:
//#line 387 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
break;
case 179:
//#line 391 "grammer.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 180:
//#line 392 "grammer.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 181:
//#line 393 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 182:
//#line 394 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 183:
//#line 395 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 186:
//#line 400 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
break;
case 187:
//#line 401 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
break;
case 188:
//#line 402 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
break;
case 189:
//#line 406 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE."); tercetos.backPatching(0); tercetos.addLabel();}
break;
case 190:
//#line 407 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
break;
case 192:
//#line 414 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 193:
//#line 415 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 194:
//#line 416 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 195:
//#line 417 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 196:
//#line 418 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 197:
//#line 419 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 198:
//#line 420 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 199:
//#line 421 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 200:
//#line 422 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 201:
//#line 423 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 202:
//#line 424 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 207:
//#line 440 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 208:
//#line 441 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 209:
//#line 442 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 210:
//#line 443 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
case 211:
//#line 444 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1572 "Parser.java"
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
