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
import GWebAssembly.GeneradorAssembler;
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
    7,    8,    8,   10,   10,   11,   12,   12,   12,   13,
   13,   17,   17,   18,   18,   18,   18,   18,   18,   18,
   19,   20,   14,   22,   24,   25,   25,   25,   25,   25,
   26,   23,   23,   27,   29,   15,   15,    9,   31,   31,
   31,    4,   33,   33,   33,   33,   34,   34,   35,   35,
   36,   37,   37,   37,    5,    5,    5,    5,    5,   38,
   38,   39,   39,   40,   41,   42,   42,   43,   44,   44,
   46,   47,   47,   48,   48,   48,   49,   49,   49,   49,
   49,   21,   51,   21,   21,   21,   21,   21,   21,   50,
   50,   50,   52,   52,   52,   52,   53,   53,   53,   53,
   54,   54,   54,   54,   54,   54,   45,   45,   45,   45,
   45,   45,   45,   55,   55,   55,   55,   55,   55,   16,
   16,   56,   30,   57,   57,   58,   58,   59,   32,   28,
   28,   28,   28,   28,   28,   28,   61,   61,   60,   60,
   62,   62,    6,    6,   63,   63,   63,   63,   63,   63,
   64,   72,   65,   65,   65,   65,   65,   65,   73,   73,
   73,   70,   70,   74,   74,   71,   66,   66,   75,   75,
   75,   75,   76,   76,   76,   76,   76,   77,   77,   77,
   77,   77,   78,   78,   78,   78,   78,   79,   67,   67,
   68,   68,   68,   68,   68,   68,   68,   68,   68,   68,
   68,   80,   81,   83,   82,   69,   69,   69,   69,   69,
};
final static short yylen[] = {                            2,
    3,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    1,    3,    3,    1,    2,    1,    1,    1,    1,    3,
    2,    1,    3,    1,    3,    4,    3,    3,    3,    3,
    1,    1,    2,    2,    1,    4,    4,    5,    3,    3,
    1,    1,    1,    2,    1,    2,    4,    2,    1,    3,
    3,    3,    3,    3,    2,    2,    1,    2,    1,    1,
    2,    3,    2,    3,    5,    6,    6,    6,    4,    3,
    3,    1,    2,    1,    2,    1,    1,    3,    1,    1,
    3,    1,    1,    1,    3,    3,    1,    3,    3,    3,
    3,    1,    0,    5,    4,    4,    3,    3,    1,    1,
    3,    3,    1,    3,    3,    3,    1,    3,    2,    1,
    1,    1,    1,    2,    2,    2,    1,    1,    2,    1,
    1,    1,    1,    4,    3,    5,    4,    3,    5,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    5,
    3,    5,    4,    4,    2,    2,    3,    2,    1,    2,
    1,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    2,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    2,    2,    1,    1,    1,    5,    4,    3,    3,
    3,    2,    1,    1,    1,    2,    2,    1,    1,    1,
    2,    2,    1,    1,    1,    2,    2,    3,    5,    4,
   12,   12,   12,   12,   13,    8,    8,   11,   11,   11,
   11,    1,    1,    1,    1,    3,    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   35,  137,  136,  138,    0,    0,    0,  176,    0,    3,
    5,    6,    7,    8,  167,    0,    0,    0,  169,    0,
  174,    0,    0,    0,  175,  130,  132,  134,  135,  153,
  154,  164,  165,  166,  168,  171,  170,    0,  163,    0,
    0,    0,  212,    0,   11,    0,    0,    0,    0,    0,
  133,    0,    0,    0,    0,  220,    0,    0,    0,  145,
  149,    0,    0,  146,    0,    1,    4,   31,    0,   22,
    0,   43,   33,   42,   41,   34,    0,    0,  120,  121,
  123,  122,  118,  117,    0,    0,    0,  161,  172,  173,
  219,    0,    0,    0,    0,    0,    9,    0,    0,    0,
   52,  110,  111,  112,  113,    0,    0,    0,    0,    0,
    0,  103,  107,    0,  182,    0,    0,    0,    0,    0,
    0,  155,  156,  157,  158,  159,  160,    0,    0,    0,
    0,    0,    0,  218,  217,  216,   99,    0,  125,   45,
    0,    0,    0,    0,  141,  150,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  119,   78,  128,
    0,   81,    0,    0,   69,    0,  139,    0,   49,    0,
   14,   16,   17,   18,   19,    0,    0,    0,   10,   55,
    0,    0,  131,    0,   57,   59,   60,   56,    0,  114,
  116,  115,  109,    0,    0,    0,  181,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  180,  179,    0,    0,
  148,    0,    0,  151,    0,  178,    0,    0,  200,    0,
    0,    0,    0,    0,    0,    0,  124,  143,    0,  144,
    0,   23,    0,   27,   32,   28,   30,   29,   25,   40,
    0,    0,   39,    0,    0,  127,    0,    0,   72,   74,
    0,  213,    0,    0,    0,   12,   15,    0,   46,    0,
   13,    0,    0,   53,   58,   54,  108,    0,    0,    0,
    0,    0,    0,    0,    0,  104,  105,  106,  147,  152,
  177,    0,  194,  193,  195,  198,  199,    0,    0,    0,
    0,   65,    0,   97,    0,   98,    0,  126,  140,  142,
   26,   44,   37,    0,   36,  129,   77,   76,   75,   70,
   73,   71,    0,    0,   51,   50,   20,    0,   62,   64,
  196,  197,    0,    0,   67,   68,   66,   96,   95,   93,
   38,  215,    0,    0,   47,    0,    0,    0,   94,    0,
    0,  206,  207,    0,    0,  214,    0,    0,    0,    0,
    0,    0,    0,    0,  210,  211,  208,  209,    0,    0,
  203,  204,    0,  201,  202,  205,
};
final static short yydgoto[] = {                          3,
   19,   20,   21,   22,   23,   24,   56,  107,  108,  180,
  181,  182,  183,   25,  185,   26,   79,   80,   81,  244,
  245,   27,   83,   28,   86,   87,  252,   29,  151,   30,
  178,  179,  111,  194,  195,  196,  197,  175,  258,  259,
  260,  319,   31,   32,   95,   33,   34,  118,  119,  152,
  349,  121,  122,  123,   35,   36,   37,   38,   39,   72,
  129,  223,  130,   40,   41,  132,  133,  134,  135,  136,
  137,   48,   49,   50,   60,  138,  139,  296,  140,   54,
  263,  343,  357,
};
final static short yysindex[] = {                       -95,
    0,   59,    0, -130, -240, -236,   -7, -189, -168,    1,
    0,    0,    0,    0,   60,  107,  133,    0,   33,    0,
    0,    0,    0,    0,    0, -176,    6, -167,    0,    0,
    0,  455,   82,  155,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  128,    0,   30,
  135,  149,    0,  -39,    0,  -26,    8,   -6,  -16,  280,
    0,  -27, -193,  274,  -34,    0,  -38,  -69,  281,    0,
    0,  158,  342,    0,  181,    0,    0,    0,  348,    0,
  484,    0,    0,    0,    0,    0,   12,  351,    0,    0,
    0,    0,    0,    0,  -28,  -32,  144,    0,    0,    0,
    0,   14,  422,  198,  326,  326,    0,   15,  -89,  175,
    0,    0,    0,    0,    0,  206,  -22, -103,  252,  126,
  301,    0,    0,  440,    0,  125,    5,  329,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   32,  223,   44,
  217,  432,  -43,    0,    0,    0,    0,   16,    0,    0,
  114,  126,  375,  458,    0,    0,  471,  470, -176,  449,
  -28,  -28,  -28,  -28,  -28,  -84,  241,    0,    0,    0,
  116,    0,  245,  245,    0,  109,    0,   31,    0,  -72,
    0,    0,    0,    0,    0, -176,   34,  188,    0,    0,
 -176, -167,    0,  390,    0,    0,    0,    0,  221,    0,
    0,    0,    0,  482,   -6,   -6,    0,   -6,   -6,   -6,
   -6,   -6,   -6,   -6,   -6,   -6,    0,    0,    0,    0,
    0,    0,  355,    0,  483,    0,  344,  485,    0,  498,
   14,  488,  -29, -104,  -10,  499,    0,    0,  418,    0,
  506,    0,  -28,    0,    0,    0,    0,    0,    0,    0,
 -176,  424,    0,  118,  509,    0,    7, -105,    0,    0,
  -36,    0,   39,  198,  198,    0,    0,   48,    0,  298,
    0,  348,  141,    0,    0,    0,    0,  252,  252,  126,
  126,  126,  126,  301,  301,    0,    0,    0,    0,    0,
    0,   42,    0,    0,    0,    0,    0,   50,  526,   14,
  528,    0,  437,    0,  536,    0,  434,    0,    0,    0,
    0,    0,    0,  538,    0,    0,    0,    0,    0,    0,
    0,    0,  109,  109,    0,    0,    0,  537,    0,    0,
    0,    0,  541,  373,    0,    0,    0,    0,    0,    0,
    0,    0,  539,  529,    0,  416,  109,  109,    0,  109,
  109,    0,    0,  540,  530,    0,  546,  551,  109,  109,
  416,  416,  553,  554,    0,    0,    0,    0,  416,  429,
    0,    0,   13,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  295,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  430,
    0,    0,  476,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  340,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  555,    0,
  101,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   28,  451,
   74,    0,    0,    0,    0,    0,    0,    0,   40,   66,
  153,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   84,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   18,    0,    0,
    0,    0,    0,    0,    0,    0,  318,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  168,  311,
    0,  525,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  194,    0,    0,
    0,  228,  255,    0,    0,    0,    0,  357,  359,  466,
  493,  501,  518,  361,  384,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  588,    0,    0,    0,  148,    0,  502,    0,  507,
 -108,    0,    0,  -68,    0,  670,  -65,  456,  363,  -97,
  -35, -147,    0,  -67,  433,    0,  475,  -21,  548,   -4,
    0,  -50,    0,  542,  -19,    0,    0, -152,  472,  -80,
    0,    0,    0,    0,    0,    0,    0,  589,  234,  616,
    0,  235,  279,  469,    0,    0,    0,    0,    0,  630,
  337,    0,  -83,  -44,    0,  743,  766,  784,  828,  838,
  844,    0,    0,    0,    0,    0,    0,    0,    0,  641,
  352, -239, -289,
};
final static int YYTABLESIZE=919;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         53,
   66,  117,  149,   53,  322,   84,  116,  117,  170,  146,
  174,  117,  116,  106,  233,  131,  116,  117,  203,  320,
  304,  207,  116,  117,  125,  257,  257,    2,  116,  117,
  306,  150,   59,  117,  116,  190,  184,  184,  116,   55,
  250,  192,  192,   57,  224,   17,   17,  110,  220,   82,
  317,  167,  266,  174,  106,  235,  376,   48,  143,  169,
  150,  358,  142,   53,  246,  247,  248,  249,   84,  363,
  364,  267,   17,   99,  264,  226,   18,  269,  299,  267,
  302,  204,  323,  184,  344,  332,   61,  229,  100,  265,
   61,  327,  270,  173,  116,   63,  105,  324,   17,   67,
  187,  187,   18,   78,  193,  193,  159,  354,  355,  183,
  257,  184,   85,  257,  100,   58,  100,  100,  100,  184,
  268,   96,   53,  222,   92,  272,  192,   92,   16,   16,
  109,  192,  100,  100,  166,  100,  173,  105,  234,  290,
   48,   24,   92,  294,   24,  311,   17,  336,   51,   52,
   18,  303,   84,  116,  237,   16,  256,   76,  315,   24,
    1,  193,  193,   71,   71,  218,  205,  206,  212,   11,
  213,   98,   17,   74,  275,  187,   18,  321,  101,  275,
  321,   16,  295,  187,  329,   11,   12,   13,   14,  193,
   61,   12,   13,   14,  193,   61,  185,   17,  100,  330,
   97,   18,   11,   12,   13,   14,  102,   61,   92,   51,
   61,  186,  232,  325,  326,  198,  103,  147,  222,  156,
   17,  145,  156,  147,   18,   24,  301,  147,  271,   16,
  148,   70,  104,  147,   21,  318,  148,  141,   11,  124,
  148,  112,  113,  114,  115,  305,  148,  112,  113,  114,
  115,  112,  113,  114,  115,   16,   64,  112,  113,  114,
  115,  276,  353,  112,  113,  114,  115,  219,   61,  112,
  113,  114,  115,  112,  113,  114,  115,  366,  368,   65,
   16,  253,  155,   51,   61,  372,  375,   53,    4,    5,
    6,   51,   61,    7,  225,   63,    8,   84,   84,    9,
   10,  189,  184,   16,  331,  333,  228,   11,   12,   13,
   14,  210,   15,  211,    4,    5,    6,  144,   21,    7,
   51,   61,    8,   18,  153,    9,   10,  188,  183,  100,
  113,  114,  115,   11,   12,   13,   14,  216,   15,   92,
  133,  222,  214,  100,  100,  100,  100,  215,  100,  100,
  100,  100,   61,  100,  187,  133,  222,  222,   92,   92,
   92,   92,   68,   92,  222,  222,   69,    7,   53,  236,
    8,  255,   18,  314,   10,   24,   24,   24,   24,   63,
   24,   11,   12,   13,   14,  157,   15,   18,   68,  113,
  114,  115,   73,    7,  205,  206,    8,   85,   18,   86,
   10,  101,  128,  101,  101,  101,  159,   11,   12,   13,
   14,  168,   15,   68,  190,  185,  347,  154,    7,  101,
  101,    8,  101,  172,  102,   10,  102,  102,  102,  191,
  186,  348,   11,   12,   13,   14,   68,   15,  278,  279,
  158,    7,  102,  102,    8,  102,  284,  285,   10,   11,
   12,   13,   14,  221,   61,   11,   12,   13,   14,   18,
   15,  176,   11,   12,   13,   14,  128,   61,   21,   21,
   21,   21,   18,   21,  340,   82,  212,  177,  213,  289,
  217,   85,  230,   86,  227,  101,  200,  201,  202,  231,
   79,   87,  286,  287,  288,   11,   12,   13,   14,  238,
   61,  239,   61,   61,   61,   61,   90,   61,  102,  243,
   87,  240,   87,  241,  274,   94,   12,   13,   14,   11,
   61,   83,  277,  208,  209,   90,  291,   90,  297,   63,
   63,   63,   63,   91,   63,  127,   80,  298,  128,  308,
    7,   88,  309,    8,  165,  300,  310,   10,  313,  316,
  133,  128,   91,  328,   91,   12,   13,   14,   89,   15,
   88,  338,   88,  293,  133,  133,  133,  133,  133,  335,
   82,  337,  192,  187,  133,   87,  339,   89,  341,   89,
  345,  346,  350,  359,   68,   79,  361,  351,  360,    7,
   90,  362,    8,  369,  370,  133,   10,  131,  162,  292,
   11,   12,   13,   14,    7,   61,   77,    8,   15,  189,
   68,   10,  188,  312,  242,    7,  101,   91,    8,   12,
   13,   14,   10,   15,  273,   88,   85,   85,   86,   86,
  101,  101,  101,  101,   15,  101,  101,  101,  101,  102,
  101,  254,   89,  171,  262,  261,   75,  126,   62,  334,
    0,  199,    0,  102,  102,  102,  102,    0,  102,  102,
  102,  102,    0,  102,   11,   12,   13,   14,    0,   61,
    0,   68,    0,  120,  120,    0,    7,    0,    0,    8,
    0,    0,  352,   10,  373,   79,    0,    0,    0,    7,
    0,    0,    8,    0,    0,   15,   10,  365,  367,   79,
   79,   79,   79,   79,    0,  371,  374,    0,   15,  131,
   88,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   87,   87,   87,   87,   89,   90,   91,   92,   93,    0,
    0,   80,    0,    0,    0,   90,   90,   90,   90,  160,
    0,    0,    0,    0,   42,   80,   80,   80,   80,   80,
    0,    0,    0,  161,  162,  163,  164,    0,   42,   42,
    0,   42,   91,   91,   91,   91,  262,   43,    0,    0,
   88,   88,   88,   88,  186,  186,    0,    0,  191,  191,
   79,   43,   43,    0,   43,   44,    0,   89,   89,   89,
   89,  342,  342,    0,   79,   79,   79,   79,   79,   44,
   44,    0,   44,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   42,  342,  342,   42,  356,  356,
  120,  120,    0,  280,  281,  282,  283,  356,  356,   45,
    0,    0,    0,    0,    0,  251,  251,   43,    0,   46,
   43,    0,    0,   45,   45,   47,   45,    0,    0,  186,
  307,    0,    0,   46,   46,   44,   46,  186,   44,   47,
   47,    0,   47,  191,    0,    0,    0,    0,  191,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   45,
    0,    0,   45,    0,    0,    0,    0,    0,    0,   46,
    0,    0,   46,    0,    0,   47,    0,    0,   47,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          4,
    0,   40,   41,    8,   41,   27,   45,   40,   41,   44,
   40,   40,   45,   40,   58,   60,   45,   40,   41,  125,
  125,  125,   45,   40,   41,  173,  174,  123,   45,   40,
   41,   67,   40,   40,   45,  125,  105,  106,   45,  280,
  125,  109,  110,  280,  128,   40,   40,   40,   44,   44,
   44,   40,  125,   40,   40,   40,   44,   40,   63,   95,
   96,  351,  256,   68,  162,  163,  164,  165,   41,  359,
  360,  180,   40,   44,   44,   44,   44,   44,  231,  188,
  233,  117,   44,   44,  324,   44,  280,   44,   59,   59,
  280,   44,   59,  123,   45,  264,  123,   59,   40,   40,
  105,  106,   44,  280,  109,  110,   59,  347,  348,   44,
  258,  180,  280,  261,   41,  123,   43,   44,   45,  188,
  186,   40,  127,  128,   41,  191,  194,   44,  123,  123,
  123,  199,   59,   60,  123,   62,  123,  123,  123,  223,
  123,   41,   59,  227,   44,  243,   40,  300,  279,  280,
   44,  256,  125,   45,   41,  123,   41,  125,   41,   59,
  256,  166,  167,   16,   17,   41,  270,  271,   43,  275,
   45,   44,   40,   41,  194,  180,   44,  258,   44,  199,
  261,  123,  227,  188,   44,  275,  276,  277,  278,  194,
  280,  276,  277,  278,  199,  280,   44,   40,  125,   59,
   46,   44,  275,  276,  277,  278,   58,  280,  125,  279,
  280,   44,  256,  264,  265,   41,  256,  256,  223,   72,
   40,  256,   75,  256,   44,  125,  256,  256,   41,  123,
  269,  125,  259,  256,   41,  257,  269,  265,  275,  256,
  269,  280,  281,  282,  283,  256,  269,  280,  281,  282,
  283,  280,  281,  282,  283,  123,  256,  280,  281,  282,
  283,   41,  346,  280,  281,  282,  283,  263,   41,  280,
  281,  282,  283,  280,  281,  282,  283,  361,  362,  279,
  123,   41,  125,  279,  280,  369,  370,  292,  256,  257,
  258,  279,  280,  261,  263,   41,  264,  270,  271,  267,
  268,  262,  263,  123,  263,  256,  263,  275,  276,  277,
  278,   60,  280,   62,  256,  257,  258,   44,  125,  261,
  279,  280,  264,   44,   44,  267,  268,  262,  263,  256,
  281,  282,  283,  275,  276,  277,  278,   37,  280,  256,
   46,  346,   42,  270,  271,  272,  273,   47,  275,  276,
  277,  278,  125,  280,   44,   61,  361,  362,  275,  276,
  277,  278,  256,  280,  369,  370,  260,  261,  373,  256,
  264,  256,   44,  256,  268,  275,  276,  277,  278,  125,
  280,  275,  276,  277,  278,   44,  280,   44,  256,  281,
  282,  283,  260,  261,  270,  271,  264,   41,   44,   41,
  268,   41,  123,   43,   44,   45,   59,  275,  276,  277,
  278,   61,  280,  256,  262,  263,   44,  260,  261,   59,
   60,  264,   62,  280,   41,  268,   43,   44,   45,  262,
  263,   59,  275,  276,  277,  278,  256,  280,  205,  206,
  260,  261,   59,   60,  264,   62,  212,  213,  268,  275,
  276,  277,  278,  125,  280,  275,  276,  277,  278,   44,
  280,   40,  275,  276,  277,  278,  123,  280,  275,  276,
  277,  278,   44,  280,   41,   46,   43,  280,   45,  125,
   41,  125,  266,  125,  262,  125,  281,  282,  283,   58,
   61,   41,  214,  215,  216,  275,  276,  277,  278,  125,
  280,   44,  275,  276,  277,  278,   41,  280,  125,   61,
   60,   41,   62,   44,  125,   61,  276,  277,  278,  275,
  280,   46,   41,  272,  273,   60,   44,   62,   44,  275,
  276,  277,  278,   41,  280,  256,   61,   40,  123,   41,
  261,   41,  125,  264,   61,   58,   41,  268,  125,   41,
  256,  123,   60,  256,   62,  276,  277,  278,   41,  280,
   60,  125,   62,  227,  270,  271,  272,  273,  274,   44,
   46,   44,  262,  263,  280,  125,   41,   60,   41,   62,
   44,   41,   44,   44,  256,   61,   41,   59,   59,  261,
  125,   41,  264,   41,   41,  256,  268,  280,   44,  256,
  275,  276,  277,  278,  261,  280,   19,  264,  280,  108,
  256,  268,  106,  251,  159,  261,  256,  125,  264,  276,
  277,  278,  268,  280,  192,  125,  270,  271,  270,  271,
  270,  271,  272,  273,  280,  275,  276,  277,  278,  256,
  280,  167,  125,   96,  176,  174,   17,   59,    8,  298,
   -1,  110,   -1,  270,  271,  272,  273,   -1,  275,  276,
  277,  278,   -1,  280,  275,  276,  277,  278,   -1,  280,
   -1,  256,   -1,   58,   59,   -1,  261,   -1,   -1,  264,
   -1,   -1,  346,  268,  256,  256,   -1,   -1,   -1,  261,
   -1,   -1,  264,   -1,   -1,  280,  268,  361,  362,  270,
  271,  272,  273,  274,   -1,  369,  370,   -1,  280,  280,
  256,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  270,  271,  272,  273,  270,  271,  272,  273,  274,   -1,
   -1,  256,   -1,   -1,   -1,  270,  271,  272,  273,  256,
   -1,   -1,   -1,   -1,    2,  270,  271,  272,  273,  274,
   -1,   -1,   -1,  270,  271,  272,  273,   -1,   16,   17,
   -1,   19,  270,  271,  272,  273,  298,    2,   -1,   -1,
  270,  271,  272,  273,  105,  106,   -1,   -1,  109,  110,
  256,   16,   17,   -1,   19,    2,   -1,  270,  271,  272,
  273,  323,  324,   -1,  270,  271,  272,  273,  274,   16,
   17,   -1,   19,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   72,  347,  348,   75,  350,  351,
  205,  206,   -1,  208,  209,  210,  211,  359,  360,    2,
   -1,   -1,   -1,   -1,   -1,  166,  167,   72,   -1,    2,
   75,   -1,   -1,   16,   17,    2,   19,   -1,   -1,  180,
  235,   -1,   -1,   16,   17,   72,   19,  188,   75,   16,
   17,   -1,   19,  194,   -1,   -1,   -1,   -1,  199,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   72,
   -1,   -1,   75,   -1,   -1,   -1,   -1,   -1,   -1,   72,
   -1,   -1,   75,   -1,   -1,   72,   -1,   -1,   75,
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
"inheritance_declaration : reference_type ','",
"inheritance_declaration : reference_type ';' error ','",
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
"$$1 :",
"arithmetic_operation : TOD '(' additive_expression ')' $$1",
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
"if_else_then_body : executable_statement",
"if_else_then_body : executable_block",
"if_else_then_body : local_variable_declaration_statement",
"if_else_then_body : error END_IF",
"if_else_then_body : error ','",
"if_else_body : executable_statement",
"if_else_body : executable_block",
"if_else_body : local_variable_declaration_statement",
"if_else_body : error END_IF",
"if_else_body : error ','",
"if_then_else_body : if_else_then_body ELSE if_else_body",
"if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ','",
"if_then_else_declaration : IF if_then_cond if_then_else_body ','",
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

//#line 460 "grammer.y"

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
    System.out.println("Tercetos\n" + tercetos.getTercetos());
    if(!Logger.errorsOcurred()){
      System.out.println("No se produjeron errores."); //Para la parte 4, generacion de codigo maquina
      GeneradorAssembler.generarCodigoAssembler(tercetos);
    }

    tercetos.printRules();
    System.out.println(aLexico.getProgram());

}


//#line 987 "Parser.java"
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
case 20:
//#line 82 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s."); yyval = new ParserVal("TablaSimbolos.agregarAtribulo($1, $2)");}
break;
case 21:
//#line 83 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La sentencia debe terminar con ','.");}
break;
case 22:
//#line 87 "grammer.y"
{yyval = new ParserVal(yyval + ";" + val_peek(0));}
break;
case 26:
//#line 93 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las declaraciones de variables se deben hacer con el caracter '='.");}
break;
case 27:
//#line 94 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter == no se permite en una declaracion.");}
break;
case 28:
//#line 95 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter !! no se permite en una declaracion.");}
break;
case 29:
//#line 96 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter <= no se permite en una declaracion.");}
break;
case 30:
//#line 97 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter >= no se permite en una declaracion.");}
break;
case 31:
//#line 100 "grammer.y"
{scope.changeScope(val_peek(0).sval);}
break;
case 36:
//#line 115 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 37:
//#line 116 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 38:
//#line 117 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 39:
//#line 118 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 40:
//#line 119 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 41:
//#line 122 "grammer.y"
{scope.stack(val_peek(0).sval);}
break;
case 46:
//#line 137 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una herencia compuesta.");}
break;
case 47:
//#line 138 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
break;
case 51:
//#line 146 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
break;
case 52:
//#line 149 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 54:
//#line 153 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 56:
//#line 155 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 63:
//#line 170 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 64:
//#line 171 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
break;
case 65:
//#line 174 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 66:
//#line 175 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 67:
//#line 176 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 68:
//#line 177 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 69:
//#line 178 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 71:
//#line 182 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 77:
//#line 196 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 78:
//#line 204 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion."); tercetos.add(val_peek(1).sval, val_peek(2).sval, val_peek(0).sval);}
break;
case 84:
//#line 218 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 85:
//#line 219 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("==", val_peek(2).sval, val_peek(0).sval));}
break;
case 86:
//#line 220 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("!!", val_peek(2).sval, val_peek(0).sval));}
break;
case 87:
//#line 223 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 88:
//#line 224 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<", val_peek(2).sval, val_peek(0).sval));}
break;
case 89:
//#line 225 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">", val_peek(2).sval, val_peek(0).sval));}
break;
case 90:
//#line 226 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 91:
//#line 227 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 92:
//#line 230 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 93:
//#line 231 "grammer.y"
{yyval = new ParserVal(tercetos.add("TOD", val_peek(1).sval, "-"));}
break;
case 94:
//#line 232 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
break;
case 95:
//#line 233 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 96:
//#line 234 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 97:
//#line 235 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 98:
//#line 236 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 99:
//#line 237 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No es una expresion aritmetica valida.");}
break;
case 100:
//#line 241 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 101:
//#line 242 "grammer.y"
{yyval = new ParserVal(tercetos.add("+", val_peek(2).sval, val_peek(0).sval));}
break;
case 102:
//#line 243 "grammer.y"
{yyval = new ParserVal(tercetos.add("-", val_peek(2).sval, val_peek(0).sval));}
break;
case 103:
//#line 246 "grammer.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 104:
//#line 247 "grammer.y"
{yyval = new ParserVal(tercetos.add("*", val_peek(2).sval, val_peek(0).sval));}
break;
case 105:
//#line 248 "grammer.y"
{yyval = new ParserVal(tercetos.add("/", val_peek(2).sval, val_peek(0).sval));}
break;
case 106:
//#line 249 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
break;
case 109:
//#line 254 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 113:
//#line 260 "grammer.y"
{yyval = new ParserVal(chequearRangoLong(val_peek(0).sval));}
break;
case 114:
//#line 261 "grammer.y"
{yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 115:
//#line 262 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 116:
//#line 263 "grammer.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 117:
//#line 268 "grammer.y"
{yyval = new ParserVal("=");}
break;
case 118:
//#line 269 "grammer.y"
{yyval = new ParserVal("-=");}
break;
case 119:
//#line 270 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 120:
//#line 271 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 121:
//#line 272 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 122:
//#line 273 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 123:
//#line 274 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 124:
//#line 277 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 125:
//#line 278 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 126:
//#line 279 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 127:
//#line 280 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 128:
//#line 281 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 129:
//#line 282 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 133:
//#line 297 "grammer.y"
{scope.changeScope(val_peek(0).sval);}
break;
case 136:
//#line 304 "grammer.y"
{yyval = new ParserVal("UINT");}
break;
case 137:
//#line 305 "grammer.y"
{yyval = new ParserVal("LONG");}
break;
case 138:
//#line 308 "grammer.y"
{yyval = new ParserVal("DOUBLE");}
break;
case 141:
//#line 321 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 142:
//#line 322 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 144:
//#line 324 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 145:
//#line 325 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 146:
//#line 326 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 161:
//#line 354 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 173:
//#line 375 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 177:
//#line 387 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF."); tercetos.backPatching(0); tercetos.addLabel();}
break;
case 178:
//#line 388 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
break;
case 179:
//#line 391 "grammer.y"
{tercetos.addCondBranch(val_peek(1).sval);}
break;
case 180:
//#line 392 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 181:
//#line 393 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
break;
case 183:
//#line 397 "grammer.y"
{tercetos.backPatching(0);}
break;
case 184:
//#line 398 "grammer.y"
{tercetos.backPatching(0);}
break;
case 185:
//#line 399 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 186:
//#line 400 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 187:
//#line 401 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 188:
//#line 404 "grammer.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 189:
//#line 405 "grammer.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 190:
//#line 406 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 191:
//#line 407 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 192:
//#line 408 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
break;
case 195:
//#line 413 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
break;
case 196:
//#line 414 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
break;
case 197:
//#line 415 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
break;
case 199:
//#line 422 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE."); tercetos.backPatching(0); tercetos.addLabel();}
break;
case 200:
//#line 423 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
break;
case 201:
//#line 427 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 202:
//#line 428 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 203:
//#line 429 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 204:
//#line 430 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 205:
//#line 431 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 206:
//#line 432 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 207:
//#line 433 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 208:
//#line 434 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 209:
//#line 435 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 210:
//#line 436 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 211:
//#line 437 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 216:
//#line 453 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 217:
//#line 454 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 218:
//#line 455 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 219:
//#line 456 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
case 220:
//#line 457 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1640 "Parser.java"
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
