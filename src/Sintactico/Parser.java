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
import GWebAssembly.GeneradorAssembler;
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
   19,   20,   14,   14,   22,   24,   25,   25,   25,   28,
   25,   26,   23,   23,   27,   30,   15,   15,   15,    9,
   32,   32,   32,    4,   34,   35,   35,   35,   35,   36,
   36,   37,   37,   37,   38,   39,   39,   39,    5,    5,
    5,    5,   41,   41,   42,   42,   43,   44,   45,   45,
   46,   46,   46,   46,   46,   46,   46,   47,   49,   50,
   50,   51,   51,   51,   52,   52,   52,   52,   52,   21,
   53,   53,   53,   54,   54,   54,   54,   55,   55,   55,
   55,   55,   57,   57,   57,   57,   57,   56,   56,   56,
   56,   56,   56,   58,   58,   58,   16,   16,   60,   61,
   61,   62,   62,   63,   33,   31,   40,   59,   48,   29,
   29,   29,   29,   29,   29,   29,   29,   29,   65,   65,
   65,   65,   65,   67,   65,   64,   64,   66,   66,    6,
    6,   68,   68,   68,   68,   68,   68,   69,   70,   70,
   70,   70,   70,   70,   77,   77,   77,   75,   75,   79,
   79,   76,   71,   71,   80,   80,   80,   80,   81,   81,
   81,   72,   83,   83,   83,   84,   84,   84,   82,   73,
   85,   85,   86,   86,   87,   87,   88,   90,   89,   78,
   91,   74,   74,   74,   74,
};
final static short yylen[] = {                            2,
    3,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    1,    3,    3,    1,    2,    1,    1,    1,    1,    3,
    2,    1,    3,    1,    3,    4,    3,    3,    3,    3,
    1,    1,    3,    2,    2,    1,    4,    3,    5,    0,
    5,    1,    1,    1,    2,    1,    2,    4,    4,    2,
    1,    3,    3,    3,    1,    3,    3,    2,    2,    1,
    2,    1,    1,    1,    2,    3,    2,    3,    5,    6,
    6,    6,    3,    3,    1,    2,    1,    2,    1,    1,
    3,    3,    3,    3,    3,    3,    3,    1,    3,    1,
    1,    1,    3,    3,    1,    3,    3,    3,    3,    1,
    1,    3,    3,    1,    3,    3,    3,    1,    1,    1,
    3,    2,    4,    4,    4,    3,    3,    1,    1,    1,
    2,    2,    2,    4,    3,    5,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    5,
    6,    4,    5,    3,    5,    4,    2,    2,    3,    2,
    5,    6,    4,    0,    6,    1,    2,    1,    2,    1,
    1,    1,    1,    1,    1,    1,    1,    3,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    2,    2,    1,
    1,    1,    5,    5,    3,    3,    3,    2,    1,    1,
    1,    5,    1,    1,    1,    1,    1,    1,    3,    6,
    1,    2,    7,    7,    1,    1,    1,    1,    1,    2,
    1,    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    0,   36,
  133,  132,  134,    0,    0,    0,  182,    0,    3,    5,
    6,    7,    8,    0,    0,    0,  175,  128,  180,    0,
   88,   91,    0,  181,    0,  127,  129,  130,  131,  160,
  161,  170,  171,  172,  174,  177,  176,  169,  173,    0,
   11,    0,   55,    0,    0,    0,    0,    0,   90,  201,
    0,    0,    0,    0,    0,  215,    0,  147,  156,    0,
    0,  148,    0,    1,    4,   31,    0,   22,    0,  211,
  210,   42,   35,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  178,  179,    0,    0,    0,    9,    0,
    0,    0,   54,    0,  118,  119,  120,    0,    0,  109,
    0,    0,    0,    0,  104,  108,  110,    0,  188,    0,
    0,    0,    0,    0,  162,  163,  164,  165,  166,  167,
    0,    0,    0,  202,    0,    0,  137,    0,  213,  212,
  214,    0,    0,  144,  157,    0,    0,  168,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   83,    0,   84,
   85,   87,   86,   82,   81,   89,  125,   46,    0,  135,
    0,   51,  136,    0,   14,   16,   17,   18,   19,    0,
    0,    0,    0,   10,   58,   64,    0,    0,    0,   60,
   62,   63,   59,    0,    0,    0,  121,  123,  122,  112,
    0,    0,    0,  187,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  186,  185,    0,  150,    0,  158,    0,
    0,    0,    0,    0,    0,    0,    0,  142,    0,    0,
  146,    0,   23,    0,   27,   32,   28,   30,   29,   25,
    0,   38,    0,    0,    0,  124,    0,    0,   12,   15,
    0,   44,    0,   43,    0,    0,   13,    0,    0,   56,
   61,   57,    0,  116,    0,  117,    0,  111,    0,    0,
    0,    0,    0,    0,    0,    0,  105,  106,  107,    0,
    0,  149,  159,  184,  183,  192,  197,  196,  198,  199,
    0,    0,    0,    0,    0,    0,    0,   69,  143,  140,
    0,  145,   26,   40,   45,    0,   37,  126,   53,   52,
   20,   33,    0,    0,   66,   68,  115,  114,  113,  153,
    0,    0,  207,    0,  205,  206,  200,    0,    0,   75,
   77,    0,   71,   72,   70,  141,   41,   39,   49,   48,
  154,  151,    0,    0,    0,   80,   79,   78,   73,   76,
   74,  155,  152,  209,    0,    0,    0,    0,  208,    0,
    0,  204,  203,
};
final static short yydgoto[] = {                          3,
   18,   19,   20,   21,   22,   69,   52,   99,  100,  174,
  175,  176,  177,  178,  179,   24,   77,   78,   79,  235,
  236,   25,  253,   26,   83,   84,  244,  337,   27,  169,
   28,  171,  172,   54,  103,  189,  190,  191,  192,  138,
  295,  329,  330,  331,  348,   29,   30,  110,   32,   61,
  111,  112,  159,  114,  115,  116,  117,   34,   35,   36,
   37,   38,   39,   70,  122,  218,  352,  219,   40,   41,
   42,   43,   44,   45,   46,   47,   48,   49,   50,   57,
  131,  132,  133,  290,   62,  292,  327,  324,  355,  360,
   81,
};
final static short yysindex[] = {                      -104,
    0,  156,    0, -201, -170,    7, -211, -125,   46,    0,
    0,    0,    0,    0,  215,  178,    0,  118,    0,    0,
    0,    0,    0,  -63,   23,  -51,    0,    0,    0,  636,
    0,    0,  109,    0,  140,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   -6,
    0,  -14,    0,   24,   91,   -8,  545,    6,    0,    0,
  109,   32, -191,  213,   37,    0,  219,    0,    0,  266,
  221,    0,  299,    0,    0,    0,   90,    0,  532,    0,
    0,    0,    0,   40,   91,   91,   91,   91,   91,   91,
   91,  -43,   81,    0,    0,   20,  517,  517,    0,   48,
 -110,  131,    0,   62,    0,    0,    0, -169,   87,    0,
  -95,  -21,   98,  478,    0,    0,    0,  267,    0,   13,
  547,    0,    0,    0,    0,    0,    0,    0,    0,    0,
 -213,   51,   49,    0,   50,  272,    0,  -50,    0,    0,
    0,  337,  293,    0,    0,  301,  300,    0,  -63,  317,
   91,   91,   91,   91,   91,  101,  193,    0,   98,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -32,    0,
  135,    0,    0,  -73,    0,    0,    0,    0,    0,  -63,
   22,  139,  387,    0,    0,    0,  -63,  -51,  -67,    0,
    0,    0,    0,  426, -100,   66,    0,    0,    0,    0,
  339,   91,   91,    0,   91,   91,   91,   91,   91,   91,
   91,   91,   91,    0,    0,  340,    0,  556,    0,  344,
  346,  348,  545,  372,   68,  357,  -33,    0,  343,  361,
    0,  377,    0,   91,    0,    0,    0,    0,    0,    0,
  294,    0,  -63,   -7,  385,    0,   20,   20,    0,    0,
  142,    0,  383,    0,  181,  184,    0,  382,  155,    0,
    0,    0,  318,    0,  408,    0,  411,    0,  -21,  -21,
   98,   98,   98,   98,  478,  478,    0,    0,    0,  580,
  401,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   72,  366,  182,  182,  415,   68,  417,    0,    0,    0,
  381,    0,    0,    0,    0,  422,    0,    0,    0,    0,
    0,    0,  405,  428,    0,    0,    0,    0,    0,    0,
  591,  604,    0,  171,    0,    0,    0,   38, -101,    0,
    0,  -39,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  625,   72,   72,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  430,  418,   72,   72,    0,  446,
  453,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  404,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  500,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   56,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  457,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -31,  707,  437,    0,    0,    0,    0,    0,    0,
    0, -214,   96,  141,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  444,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   89,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  222,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  277,    0,  310,    0,  375,    0,    0,  410,  477,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   -1,   10,
  725,  739,  750,  773,  469,  506,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  487,    0,    0,    0,    4,    0,  419,    0,  413,
  -91,    0,    0,    0,  328,  765,   73,  374,  278,  728,
  828,  350,    0,  569,  349,    0,    0,    0,  -24,    0,
  -41,    0,  -88,    0,    0,  434,  -69,    0,    0,    0,
 -192,  238, -259,    0,    0,    0,    0,   -2,    0,    2,
  482,  163,  671,  190,  296, -207,    0,    0,    0,    0,
    0,    0,    0,   12, -187, -236,    0,  -28,  -46,    0,
  -54,  -45,  -36,  -34,  -30,  -26,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  194,  187,
    0,
};
final static int YYTABLESIZE=1046;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         31,
   80,  351,  125,   33,   60,   23,  294,  227,  246,   92,
  124,  126,   31,   31,  185,   31,   33,   33,    2,   33,
  127,   23,  128,  349,  264,   98,  129,   73,  123,  204,
  130,  109,  119,  307,  298,  287,  108,   94,  207,   93,
  208,  190,  220,  321,   58,   66,   56,  194,  190,  221,
   94,  249,   95,  215,   31,  182,  182,  260,   33,  182,
  182,   16,   16,  102,  136,  252,  125,   31,   59,  350,
   31,   33,  350,  145,   33,  126,  145,   16,   51,  157,
  140,  346,  250,  323,  127,  343,  128,   98,  137,  293,
  129,  250,  139,   92,  130,  141,  139,  139,  139,  139,
  139,  196,  139,  334,  325,  109,  266,  294,   97,   53,
  108,  197,  198,  199,  139,  139,  108,  139,   31,  261,
  109,  167,   33,   93,  261,  108,  109,  200,   50,   55,
  109,  108,  182,  148,   94,  108,  354,  354,   63,   31,
  209,  182,  210,   33,   15,   15,  101,  182,  149,  359,
  359,    1,  182,  229,   92,  263,  254,   16,  309,  310,
   15,   17,  156,  125,   10,   11,   12,   13,  125,  173,
   97,  193,  126,   10,  202,  203,  289,  126,  247,   93,
  139,  127,  255,  128,  195,  311,  127,  129,  128,  283,
  293,  130,  129,  248,  288,   16,  130,  256,  315,   17,
  149,   10,   11,   12,   13,  226,  173,   10,   11,   12,
   13,   50,  173,  316,  344,   31,   76,   16,   72,   33,
   31,   17,  297,  245,   33,  125,   31,   31,   82,  345,
   33,   33,  145,  242,  126,   10,  166,  125,   92,   92,
   15,  301,   74,  127,   96,  128,  126,  118,  306,  129,
  205,  206,  251,  130,   16,  127,  139,  128,   17,  258,
  104,  129,  142,  326,  146,  130,  125,  125,   93,   93,
  134,   59,  105,  106,  107,  126,  126,   31,   15,   94,
   94,   33,  202,  203,  127,  127,  128,  128,  125,   31,
  129,  129,  283,   33,  130,  130,  135,  126,   31,  170,
   15,   64,   33,  347,  145,   16,  127,  214,  128,   17,
  223,  139,  129,  222,  283,  224,  130,   21,   31,   31,
  139,  265,   33,   33,   65,  139,  139,  139,  139,  225,
  139,  139,  139,  139,  104,  139,  230,   15,   16,   68,
   31,  231,   17,  232,   33,   59,  105,  106,  107,  104,
   34,  189,  105,  106,  107,  104,  241,  193,  189,  104,
   59,  105,  106,  107,  269,  270,   59,  105,  106,  107,
   59,  105,  106,  107,    4,    5,   16,  234,    6,  268,
   17,    7,   16,  280,    8,    9,   17,  284,   15,  285,
  144,  286,   10,   11,   12,   13,  191,   14,  275,  276,
   16,   21,  195,  191,   17,   10,   11,   12,   13,   17,
  173,  291,    4,    5,  296,   47,    6,  302,  304,    7,
   16,   15,    8,    9,   17,  308,  312,  257,  186,  186,
   10,   11,   12,   13,   34,   14,  313,   71,    6,  314,
  149,    7,  317,   90,  322,    9,  181,  181,  318,   90,
   65,  319,   10,   11,   12,   13,   10,   14,  333,   15,
  335,  228,  338,  339,   90,   15,  262,  299,   11,   12,
   13,  340,  173,  357,   67,    6,  358,  101,    7,  101,
  101,  101,    9,   15,  100,  300,  362,  100,  121,   10,
   11,   12,   13,  363,   14,  101,  101,   24,  101,   47,
   24,  128,  100,   15,   75,  336,  277,  278,  279,  102,
  183,  102,  102,  102,  213,   24,  186,   67,  184,  211,
  305,  186,  233,  181,  212,  143,    6,  102,  102,    7,
  102,  332,  181,    9,   65,  194,  259,  120,  356,  138,
   10,   11,   12,   13,  361,   14,  103,    0,  103,  103,
  103,   21,   21,   21,   21,    0,   21,    0,  147,    6,
  139,  101,    7,    0,  103,  103,    9,  103,  100,    0,
    0,    0,    0,   10,   11,   12,   13,    0,   14,    0,
    0,   24,    0,    0,   34,   34,   34,   34,   17,   34,
   17,    0,  155,  102,    0,    0,    0,    6,    0,   17,
    7,   67,    0,    6,    9,    0,    7,    0,    0,    0,
    9,   10,   11,   12,   13,    0,   14,   10,   11,   12,
   13,    6,   14,   17,    7,    0,    6,    0,    9,    7,
  103,    0,    0,    9,   17,   10,   11,   12,   13,    0,
   14,    6,  328,  328,    7,   59,    0,   17,    9,   47,
   47,   47,   47,    0,   47,   10,   11,   12,   13,   90,
   14,   10,   11,   12,   13,    0,  173,  121,   17,  188,
  188,  217,    0,   90,   90,   90,   90,   90,  328,    0,
  282,  328,    0,  136,   65,   65,   65,   65,    0,   65,
    0,    0,  101,    0,    0,    0,   91,    0,    0,  100,
   10,   11,   12,   13,  320,  173,  101,  101,  101,  101,
    0,  101,  101,  101,  101,  341,  101,    0,  100,  100,
  100,  100,    0,  100,  102,  113,  113,    0,  342,    0,
    0,   24,   24,   24,   24,    0,   24,    0,  102,  102,
  102,  102,    0,  102,  102,  102,  102,   95,  102,  353,
    0,   67,   67,   67,   67,  139,   67,  188,    0,    0,
    0,  103,  188,    0,    0,   98,   95,    0,   95,  139,
  139,  139,  139,  139,    0,  103,  103,  103,  103,   99,
  103,  103,  103,  103,   98,  103,   98,  150,    0,    0,
   96,   10,   11,   12,   13,    0,  173,    0,   99,    0,
   99,  151,  152,  153,  154,    6,  216,    6,    7,   96,
    7,   96,    9,   97,    9,  281,    6,    0,    0,    7,
   11,   12,   13,    9,   14,    0,   59,    0,    0,    0,
    0,   95,   97,    0,   97,   59,    0,    0,    0,    0,
    6,    0,    0,    7,    0,    0,    0,    9,    0,   98,
    0,    6,    0,    0,    7,    0,    0,    0,    9,   59,
    0,  180,  180,   99,    6,  187,  187,    7,    0,    0,
   59,    9,  113,  113,   96,  271,  272,  273,  274,  237,
  238,  239,  240,   59,    0,    6,    0,    0,    7,    0,
    0,   85,    9,    0,    0,    0,    0,   97,    0,    0,
    0,    0,    0,    0,   59,   86,   87,   88,   89,   90,
    0,    0,  158,  160,  161,  162,  163,  164,  165,    0,
  168,  243,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  201,    0,  180,    0,
    0,    0,    0,    0,    0,    0,    0,  180,    0,    0,
    0,    0,    0,  187,    0,    0,    0,    0,  187,    0,
    0,  303,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   95,   95,   95,   95,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   98,   98,   98,   98,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   99,   99,
   99,   99,    0,    0,    0,    0,    0,    0,    0,   96,
   96,   96,   96,  267,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   97,   97,   97,   97,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          2,
   25,   41,   57,    2,    7,    2,   40,   58,   41,   41,
   57,   57,   15,   16,  125,   18,   15,   16,  123,   18,
   57,   18,   57,  125,  125,   40,   57,   16,   57,  125,
   57,   40,   41,   41,  227,  223,   45,   44,   60,   41,
   62,  256,  256,  280,  256,    0,   40,  262,  263,  263,
   41,  125,   59,   41,   57,   97,   98,  125,   57,  101,
  102,   40,   40,   40,  256,   44,  121,   70,  280,  329,
   73,   70,  332,   70,   73,  121,   73,   40,  280,   40,
   44,   44,  174,  291,  121,  322,  121,   40,  280,  123,
  121,  183,   37,  125,  121,   59,   41,   42,   43,   44,
   45,   40,   47,  296,  292,   40,   41,   40,  123,  280,
   45,  281,  282,  283,   59,   60,   45,   62,  121,  189,
   40,   41,  121,  125,  194,   45,   40,   41,   40,  123,
   40,   45,  174,   44,  125,   45,  344,  345,  264,  142,
   43,  183,   45,  142,  123,  123,  123,  189,   59,  357,
  358,  256,  194,  142,   46,  256,  181,   40,  247,  248,
  123,   44,  123,  218,  275,  276,  277,  278,  223,  280,
  123,   41,  218,  275,  270,  271,  223,  223,   44,   40,
  125,  218,   44,  218,  123,   44,  223,  218,  223,  218,
  123,  218,  223,   59,  223,   40,  223,   59,   44,   44,
   59,  275,  276,  277,  278,  256,  280,  275,  276,  277,
  278,  123,  280,   59,   44,  218,  280,   40,   41,  218,
  223,   44,  256,  256,  223,  280,  229,  230,  280,   59,
  229,  230,  229,   41,  280,  275,  280,  292,  270,  271,
  123,  230,  125,  280,  259,  280,  292,  256,  256,  280,
  272,  273,  180,  280,   40,  292,   44,  292,   44,  187,
  269,  292,   44,  292,   44,  292,  321,  322,  270,  271,
  265,  280,  281,  282,  283,  321,  322,  280,  123,  270,
  271,  280,  270,  271,  321,  322,  321,  322,  343,  292,
  321,  322,  321,  292,  321,  322,  265,  343,  301,  280,
  123,  256,  301,  328,  301,   40,  343,   41,  343,   44,
  262,  256,  343,  263,  343,  266,  343,   41,  321,  322,
  265,  256,  321,  322,  279,  270,  271,  272,  273,   58,
  275,  276,  277,  278,  269,  280,   44,  123,   40,  125,
  343,   41,   44,   44,  343,  280,  281,  282,  283,  269,
   41,  256,  281,  282,  283,  269,  256,  262,  263,  269,
  280,  281,  282,  283,  202,  203,  280,  281,  282,  283,
  280,  281,  282,  283,  257,  258,   40,   61,  261,   41,
   44,  264,   40,   44,  267,  268,   44,   44,  123,   44,
  125,   44,  275,  276,  277,  278,  256,  280,  209,  210,
   40,  125,  262,  263,   44,  275,  276,  277,  278,   44,
  280,   40,  257,  258,   58,   41,  261,   41,  125,  264,
   40,  123,  267,  268,   44,   41,   44,   41,  101,  102,
  275,  276,  277,  278,  125,  280,  256,  260,  261,  256,
   59,  264,  125,   40,   44,  268,   97,   98,   41,   46,
   41,   41,  275,  276,  277,  278,  275,  280,   44,  123,
   44,  125,   41,   59,   61,  123,   41,  125,  276,  277,
  278,   44,  280,   44,  260,  261,   59,   41,  264,   43,
   44,   45,  268,  123,   41,  125,   41,   44,  123,  275,
  276,  277,  278,   41,  280,   59,   60,   41,   62,  125,
   44,  280,   59,  123,   18,  125,  211,  212,  213,   41,
   98,   43,   44,   45,   37,   59,  189,   41,  100,   42,
  243,  194,  149,  174,   47,  260,  261,   59,   60,  264,
   62,  294,  183,  268,  125,  102,  188,   56,  345,   40,
  275,  276,  277,  278,  358,  280,   41,   -1,   43,   44,
   45,  275,  276,  277,  278,   -1,  280,   -1,  260,  261,
   61,  125,  264,   -1,   59,   60,  268,   62,  125,   -1,
   -1,   -1,   -1,  275,  276,  277,  278,   -1,  280,   -1,
   -1,  125,   -1,   -1,  275,  276,  277,  278,   44,  280,
   44,   -1,   61,  125,   -1,   -1,   -1,  261,   -1,   44,
  264,  125,   -1,  261,  268,   -1,  264,   -1,   -1,   -1,
  268,  275,  276,  277,  278,   -1,  280,  275,  276,  277,
  278,  261,  280,   44,  264,   -1,  261,   -1,  268,  264,
  125,   -1,   -1,  268,   44,  275,  276,  277,  278,   -1,
  280,  261,  293,  294,  264,  280,   -1,   44,  268,  275,
  276,  277,  278,   -1,  280,  275,  276,  277,  278,  256,
  280,  275,  276,  277,  278,   -1,  280,  123,   44,  101,
  102,  125,   -1,  270,  271,  272,  273,  274,  329,   -1,
  125,  332,   -1,  280,  275,  276,  277,  278,   -1,  280,
   -1,   -1,  256,   -1,   -1,   -1,   61,   -1,   -1,  256,
  275,  276,  277,  278,  125,  280,  270,  271,  272,  273,
   -1,  275,  276,  277,  278,  125,  280,   -1,  275,  276,
  277,  278,   -1,  280,  256,   55,   56,   -1,  125,   -1,
   -1,  275,  276,  277,  278,   -1,  280,   -1,  270,  271,
  272,  273,   -1,  275,  276,  277,  278,   41,  280,  125,
   -1,  275,  276,  277,  278,  256,  280,  189,   -1,   -1,
   -1,  256,  194,   -1,   -1,   41,   60,   -1,   62,  270,
  271,  272,  273,  274,   -1,  270,  271,  272,  273,   41,
  275,  276,  277,  278,   60,  280,   62,  256,   -1,   -1,
   41,  275,  276,  277,  278,   -1,  280,   -1,   60,   -1,
   62,  270,  271,  272,  273,  261,  260,  261,  264,   60,
  264,   62,  268,   41,  268,  260,  261,   -1,   -1,  264,
  276,  277,  278,  268,  280,   -1,  280,   -1,   -1,   -1,
   -1,  125,   60,   -1,   62,  280,   -1,   -1,   -1,   -1,
  261,   -1,   -1,  264,   -1,   -1,   -1,  268,   -1,  125,
   -1,  261,   -1,   -1,  264,   -1,   -1,   -1,  268,  280,
   -1,   97,   98,  125,  261,  101,  102,  264,   -1,   -1,
  280,  268,  202,  203,  125,  205,  206,  207,  208,  152,
  153,  154,  155,  280,   -1,  261,   -1,   -1,  264,   -1,
   -1,  256,  268,   -1,   -1,   -1,   -1,  125,   -1,   -1,
   -1,   -1,   -1,   -1,  280,  270,  271,  272,  273,  274,
   -1,   -1,   85,   86,   87,   88,   89,   90,   91,   -1,
   93,  157,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  109,   -1,  174,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  183,   -1,   -1,
   -1,   -1,   -1,  189,   -1,   -1,   -1,   -1,  194,   -1,
   -1,  234,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  270,  271,  272,  273,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  270,  271,  272,  273,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  270,  271,
  272,  273,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  270,
  271,  272,  273,  196,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  270,  271,  272,  273,
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
"result_type : VOID",
"method_declarator : method_name '(' formal_parameter ')'",
"method_declarator : method_name '(' ')'",
"method_declarator : method_name '(' formal_parameter error ')'",
"$$1 :",
"method_declarator : method_name '{' error '}' $$1",
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
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
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
"method_invocation : reference_method '(' real_parameter ')'",
"method_invocation : reference_method '(' ')'",
"method_invocation : reference_method '(' real_parameter error ')'",
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
"reference_method : primary",
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
"$$2 :",
"executable_block : '{' RETURN ',' executable_block_statements '}' $$2",
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
"statement_expression : method_invocation",
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
"print_statement : PRINT CADENA ','",
"print_statement : PRINT error ','",
"print_statement : PRINT CADENA ';'",
"print_statement : PRINT '\\000'",
};

//#line 793 "grammar.y"

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
      GeneradorAssembler.generarCodigoAssembler(tercetos);
      System.out.println("TABLA SIMBOLOS \n" + TablaSimbolos.printTable());
      System.out.println("ASSEMBLER \n" + GeneradorAssembler.codigoAssembler);
    } else 
      System.out.println("Se produjeron errores.\n");
    

    System.out.println(aLexico.getProgram());
}

//#line 939 "Parser.java"
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
                scope.stack(val_peek(0).sval); 
                TablaSimbolos.addClase(val_peek(0).sval); 
                TablaClases.addClase(val_peek(0).sval);
                yyval = new ParserVal(val_peek(0).sval);
              } else {
                Logger.logError(aLexico.getProgramPosition(), "La clase " + val_peek(0).sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                yyval = new ParserVal("");
              }
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
                        
                        if (TablaSimbolos.isClass(val_peek(2).sval + scope.getScopeMain()))
                            TablaClases.addAtributos(val_peek(2).sval, _attributes, _class);
                        else 
                            TablaClases.addAtributos(_attributes, _class);
                      }

           
                   }
break;
case 21:
//#line 133 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 23:
//#line 137 "grammar.y"
{ yyval = new ParserVal(val_peek(2).sval + ";" + val_peek(0).sval );}
break;
case 25:
//#line 141 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 26:
//#line 142 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 27:
//#line 143 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 28:
//#line 144 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 29:
//#line 145 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 30:
//#line 146 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
break;
case 31:
//#line 149 "grammar.y"
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
//#line 163 "grammar.y"
{
                      if (!val_peek(2).sval.isEmpty()) {
                        ArrayList<String> ambitos = scope.getAmbitos(val_peek(2).sval);
                        String _method = ambitos.get(0); 
                        String _class = ambitos.get(2);

                          if (val_peek(1).sval.isEmpty()) {
                            TablaClases.addMetodoIMPL(_method, _class);
                            TablaSimbolos.setFuncPrototype(val_peek(2).sval);
                          } else {
                            TablaClases.addMetodo(_method, _class);
                          }
                      scope.deleteLastScope();
                      }
                   }
break;
case 34:
//#line 178 "grammar.y"
{
                      if (!val_peek(1).sval.isEmpty()){
                          ArrayList<String> ambitos = scope.getAmbitos(val_peek(1).sval);
                          String _method = ambitos.get(0); 
                          String _class = ambitos.get(2);

                          if (val_peek(0).sval.isEmpty()) {
                            TablaClases.addMetodoIMPL(_method, _class);
                            TablaSimbolos.setFuncPrototype(val_peek(1).sval);
                          } else {
                            TablaClases.addMetodo(_method, _class);
                          }
                      scope.deleteLastScope();
                      }
                    }
break;
case 35:
//#line 195 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 37:
//#line 201 "grammar.y"
{
                      String ref = val_peek(3).sval;
                      String par = val_peek(1).sval;
                      if (!ref.isEmpty() && !par.isEmpty()) {
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo con p/j de parametro.");
                        yyval = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref, par);
                      } else
                        Logger.logError(aLexico.getProgramPosition(), "No se reconocio un metodo con p/j de parametro.");

                  }
break;
case 38:
//#line 212 "grammar.y"
{
                      String ref = val_peek(2).sval;
                      if (!ref.isEmpty()) {
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo sin p/j de parametro.");
                        yyval = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref);
                      }
                  }
break;
case 39:
//#line 220 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 40:
//#line 221 "grammar.y"
{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
break;
case 41:
//#line 224 "grammar.y"
{



}
break;
case 42:
//#line 231 "grammar.y"
{
                if(!scope.isDeclaredInMyScope(val_peek(0).sval)){
                  if (scope.hasPassedNesting()) {
                    Logger.logError(aLexico.getProgramPosition(), "Solo se permite 1 nivel de anidamiento en los metodos.");
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
//#line 250 "grammar.y"
{yyval = new ParserVal("");}
break;
case 45:
//#line 253 "grammar.y"
{
                      if (!val_peek(0).sval.isEmpty()){
                        yyval = new ParserVal(val_peek(0).sval);
                        TablaSimbolos.addTipoVariable(val_peek(1).sval, val_peek(0).sval);
                      }
                   }
break;
case 47:
//#line 264 "grammar.y"
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
//#line 278 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
break;
case 49:
//#line 279 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
break;
case 50:
//#line 282 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); if (val_peek(0).sval.contains(";")) Logger.logError(aLexico.getProgramPosition(), "No se permite implementar multiples interfaces.");}
break;
case 52:
//#line 286 "grammar.y"
{yyval = new ParserVal(val_peek(2).sval + ";" + val_peek(0).sval);}
break;
case 53:
//#line 287 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
break;
case 54:
//#line 290 "grammar.y"
{
                          yyval = val_peek(1);
                          String name = val_peek(1).sval;
                          if (!name.isEmpty()) {
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");
                          }
                      }
break;
case 55:
//#line 299 "grammar.y"
{
                    if (!scope.isDeclaredInMyScope(val_peek(0).sval)) {
                      scope.stack(val_peek(0).sval); 
                      TablaClases.addInterface(val_peek(0).sval);
                      TablaSimbolos.addInterface(val_peek(0).sval);
                    } else {
                      Logger.logError(aLexico.getProgramPosition(), "La interface " + val_peek(0).sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                      yyval = new ParserVal("");
                    }
                }
break;
case 57:
//#line 312 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 59:
//#line 314 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 62:
//#line 321 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permite la declaracion de constantes en las interfaces.");}
break;
case 63:
//#line 322 "grammar.y"
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
//#line 333 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No esta permitida la herencia en una interface.");}
break;
case 66:
//#line 339 "grammar.y"
{yyval = val_peek(1);}
break;
case 67:
//#line 340 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 68:
//#line 341 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
break;
case 70:
//#line 345 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 71:
//#line 346 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 72:
//#line 347 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 74:
//#line 351 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 78:
//#line 361 "grammar.y"
{

                                    if (!val_peek(1).sval.isEmpty()){

                                      ArrayList<String> ambitos = scope.getAmbitos(val_peek(1).sval);
                                      if (ambitos.size() > 2) {
                                        String _method = ambitos.get(0);                                      
                                        String _class = ambitos.get(2); 

                                        if (!TablaClases.esUnMetodoAImplementar(_method, _class)){
                                          if (TablaClases.esUnMetodoConcreto(_method, _class)) {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo ya implementado (IMPL FOR)");
                                          } else {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo que no existe (IMPL FOR)");
                                          }  
                                        } else {
                                          TablaSimbolos.setImplemented(val_peek(1).sval);
                                          TablaClases.setMetodoIMPL(_method, _class);
                                        }
                                      }
                                     }
                                  }
break;
case 80:
//#line 386 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 81:
//#line 394 "grammar.y"
{
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
                tercetos.add("=", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval));
                tercetos.declaredFactorsUsed(val_peek(0).sval);
           }
break;
case 82:
//#line 399 "grammar.y"
{
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion de resta.");
                tercetos.add("=", val_peek(2).sval, tercetos.add("-", val_peek(2).sval, val_peek(0).sval));
                tercetos.declaredFactorsUsed(val_peek(0).sval);
           }
break;
case 83:
//#line 404 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 84:
//#line 405 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 85:
//#line 406 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 86:
//#line 407 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 87:
//#line 408 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 89:
//#line 415 "grammar.y"
{yyval = new ParserVal(val_peek(2).sval + "." + val_peek(0).sval);}
break;
case 92:
//#line 422 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 93:
//#line 423 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("==", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 94:
//#line 424 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("!!", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 95:
//#line 427 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 96:
//#line 428 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<", val_peek(2).sval, val_peek(0).sval));}
break;
case 97:
//#line 429 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">", val_peek(2).sval, val_peek(0).sval));}
break;
case 98:
//#line 430 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add(">=", val_peek(2).sval, val_peek(0).sval));}
break;
case 99:
//#line 431 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); yyval = new ParserVal(tercetos.add("<=", val_peek(2).sval, val_peek(0).sval));}
break;
case 100:
//#line 434 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 101:
//#line 437 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 102:
//#line 438 "grammar.y"
{yyval = new ParserVal(tercetos.add("+", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 103:
//#line 439 "grammar.y"
{yyval = new ParserVal(tercetos.add("-", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 104:
//#line 442 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 105:
//#line 443 "grammar.y"
{yyval = new ParserVal(tercetos.add("*", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 106:
//#line 444 "grammar.y"
{yyval = new ParserVal(tercetos.add("/", val_peek(2).sval, val_peek(0).sval, tercetos.typeTerceto(val_peek(2).sval, val_peek(0).sval)));}
break;
case 107:
//#line 445 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
break;
case 111:
//#line 451 "grammar.y"
{
                    if (tercetos.hasNestingExpressions(val_peek(1).sval)) 
                      Logger.logError(aLexico.getProgramPosition(), "No se permite el anidamiento de expresiones.");
                    yyval = new ParserVal(val_peek(1).sval);  
                 }
break;
case 112:
//#line 456 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 113:
//#line 459 "grammar.y"
{
                          yyval = new ParserVal(tercetos.add("TOD", val_peek(1).sval, "-"));
                          tercetos.TODtracking(yyval.sval);
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");
                       }
break;
case 114:
//#line 464 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 115:
//#line 465 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 116:
//#line 466 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 117:
//#line 467 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 118:
//#line 470 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval); }
break;
case 119:
//#line 471 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 120:
//#line 472 "grammar.y"
{yyval = new ParserVal(TablaTipos.chequearRangoLong(val_peek(0).sval, aLexico.getProgramPosition()));}
break;
case 121:
//#line 473 "grammar.y"
{ yyval = new ParserVal(TablaTipos.negarDouble(val_peek(0).sval));

                       }
break;
case 122:
//#line 477 "grammar.y"
{
                        if(!TablaTipos.chequearRangoLongNegativo(val_peek(0).sval)){
                            Logger.logWarning(aLexico.getProgramPosition(),"LONG NEGATIVO FUERA DE RANGO SE TRUNCA AL MINIMO PERMITIDO");
                            yyval = new ParserVal(TablaTipos.negarLong("2147483648"));
                        } else{
                            yyval = new ParserVal(TablaTipos.negarLong(val_peek(0).sval));
                        }
                     }
break;
case 123:
//#line 486 "grammar.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 124:
//#line 489 "grammar.y"
{
                    String ref = val_peek(3).sval;
                    if ( !ref.isEmpty() ){
                      yyval = new ParserVal(ref);
                      
                      if (!tercetos.linkInvocation(ref, val_peek(1).sval))
                        Logger.logError(aLexico.getProgramPosition(), "El metodo a invocar no posee parametro formal.");
                      else
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");
                    } 
                  }
break;
case 125:
//#line 500 "grammar.y"
{
                    String ref = val_peek(2).sval;
                    if (!ref.isEmpty()){
                      yyval = new ParserVal(ref);
                      
                      if (!tercetos.linkInvocation(ref))
                        Logger.logError(aLexico.getProgramPosition(), "El metodo a invocar no posee parametro formal.");
                      else
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");
                    } 
                  }
break;
case 126:
//#line 511 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 132:
//#line 529 "grammar.y"
{yyval = new ParserVal("UINT");}
break;
case 133:
//#line 530 "grammar.y"
{yyval = new ParserVal("LONG");}
break;
case 134:
//#line 533 "grammar.y"
{yyval = new ParserVal("DOUBLE");}
break;
case 135:
//#line 537 "grammar.y"
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
case 136:
//#line 548 "grammar.y"
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
case 137:
//#line 560 "grammar.y"
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
case 138:
//#line 574 "grammar.y"
{
                    String reference = scope.searchFunc(val_peek(0).sval);

                    if(reference == null) {
                      Logger.logError(aLexico.getProgramPosition(), "El metodo " + val_peek(0).sval + " no esta al alcance.");
                      yyval = new ParserVal("");
                    }
                    else
                      yyval = new ParserVal(reference);
               }
break;
case 139:
//#line 587 "grammar.y"
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
case 140:
//#line 603 "grammar.y"
{tercetos.addReturn();}
break;
case 141:
//#line 604 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 142:
//#line 605 "grammar.y"
{tercetos.addReturn();}
break;
case 143:
//#line 606 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 144:
//#line 607 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 145:
//#line 608 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 146:
//#line 609 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 147:
//#line 610 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 148:
//#line 611 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 151:
//#line 616 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 152:
//#line 617 "grammar.y"
{tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 153:
//#line 618 "grammar.y"
{tercetos.addReturn();}
break;
case 154:
//#line 619 "grammar.y"
{tercetos.addReturn();}
break;
case 155:
//#line 621 "grammar.y"
{Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
break;
case 168:
//#line 646 "grammar.y"
{
                              if (!(val_peek(2).sval.isEmpty() || val_peek(1).sval.isEmpty())) {
                                TablaSimbolos.addTipoVariable(val_peek(2).sval, val_peek(1).sval);                      

                                if (TablaSimbolos.isClass(val_peek(2).sval + scope.getScopeMain())) {
                                  TablaClases.addInstancia(val_peek(2).sval, val_peek(1).sval);
                                }
                                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");
                              }
                            }
break;
case 179:
//#line 674 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 183:
//#line 685 "grammar.y"
{
                         tercetos.backPatching(0);
                         tercetos.addLabel();
                         Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 184:
//#line 689 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con la palabra reservada END_IF.");}
break;
case 185:
//#line 692 "grammar.y"
{tercetos.addCondBranch(val_peek(1).sval);}
break;
case 186:
//#line 693 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 187:
//#line 694 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
break;
case 191:
//#line 700 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 192:
//#line 703 "grammar.y"
{
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
                            tercetos.backPatching(0);
                            tercetos.addLabel();
                          }
break;
case 193:
//#line 710 "grammar.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 194:
//#line 711 "grammar.y"
{tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
break;
case 195:
//#line 712 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
break;
case 198:
//#line 717 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
break;
case 200:
//#line 723 "grammar.y"
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
case 201:
//#line 737 "grammar.y"
{
                               if (!val_peek(0).sval.isEmpty()) {
                                yyval = new ParserVal(val_peek(0).sval);
                                tercetos.add("=", val_peek(0).sval, "-");
                                tercetos.stack(val_peek(0).sval);
                                tercetos.stack();
                               }
                         }
break;
case 202:
//#line 745 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Error en la signatura del FOR IN RANGE.");}
break;
case 203:
//#line 748 "grammar.y"
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
case 204:
//#line 763 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 207:
//#line 770 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 208:
//#line 773 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 209:
//#line 776 "grammar.y"
{yyval = new ParserVal(val_peek(0).sval);}
break;
case 210:
//#line 779 "grammar.y"
{
                        scope.deleteLastScope();
                      }
break;
case 212:
//#line 787 "grammar.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT."); tercetos.add("PRINT", val_peek(1).sval, "-");}
break;
case 213:
//#line 788 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena en la sentencia PRINT.");}
break;
case 214:
//#line 789 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 215:
//#line 790 "grammar.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1973 "Parser.java"
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
