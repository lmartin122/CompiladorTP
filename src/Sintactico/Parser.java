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

import GCodigo.PolacaInversa;
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
   47,   47,   47,   48,   48,   48,   48,   48,   20,   50,
   20,   20,   20,   20,   20,   20,   49,   49,   49,   51,
   51,   51,   51,   52,   52,   52,   52,   53,   53,   53,
   53,   53,   53,   44,   44,   44,   44,   44,   44,   44,
   54,   54,   54,   54,   54,   54,   15,   15,   55,   36,
   56,   56,   57,   57,   58,   30,   27,   27,   27,   27,
   27,   27,   27,   60,   60,   59,   59,   61,   61,    6,
    6,   62,   62,   62,   62,   62,   62,   63,   71,   64,
   64,   64,   64,   64,   64,   72,   72,   72,   69,   69,
   73,   73,   70,   65,   65,   65,   65,   65,   65,   65,
   66,   66,   66,   66,   66,   66,   66,   66,   66,   67,
   67,   67,   67,   67,   67,   67,   67,   67,   67,   67,
   74,   75,   77,   76,   68,   68,   68,   68,   68,
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
    1,    3,    3,    1,    3,    3,    3,    3,    1,    0,
    5,    4,    4,    3,    3,    1,    1,    3,    3,    1,
    3,    3,    3,    1,    3,    2,    1,    1,    1,    1,
    2,    2,    2,    1,    1,    2,    1,    1,    1,    1,
    4,    3,    5,    4,    3,    5,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    5,    3,    5,    4,
    4,    2,    2,    3,    2,    1,    2,    1,    2,    1,
    1,    1,    1,    1,    1,    1,    1,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    2,    2,
    1,    1,    1,    7,    7,    6,    6,    7,    7,    7,
    9,    9,    9,    9,    9,    9,    9,    9,    9,   12,
   12,   12,   12,   13,    8,    8,   11,   11,   11,   11,
    1,    1,    1,    1,    3,    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   34,  134,  133,  135,    0,    0,    0,  173,    0,    3,
    5,    6,    7,    8,  164,    0,    0,    0,  166,    0,
  171,    0,    0,    0,  172,  127,  129,  131,  132,  150,
  151,  161,  162,  163,  165,  168,  167,    0,  160,    0,
    0,    0,  201,    0,   11,    0,    0,    0,  130,    0,
    0,    0,    0,  209,    0,    0,    0,  142,  146,    0,
    0,  143,    0,    1,    4,   30,    0,   21,    0,   42,
   32,   41,   40,   33,    0,    0,  117,  118,  120,  119,
  115,  114,    0,    0,    0,  158,  169,  170,  208,    0,
    0,    0,    0,    0,    9,    0,    0,    0,   49,    0,
  107,  108,  109,  110,    0,    0,    0,    0,    0,    0,
  100,  104,    0,    0,    0,  207,  206,  205,   96,    0,
  122,   44,    0,    0,    0,    0,  138,  147,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  116,
   75,  125,    0,   78,    0,    0,   66,    0,  136,    0,
   46,    0,   14,   16,   17,   18,    0,  128,    0,   10,
   52,    0,    0,    0,   54,   56,   57,   53,    0,    0,
  111,  113,  112,  106,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  121,  140,    0,  141,    0,   22,
    0,   26,   31,   27,   29,   28,   24,   39,    0,    0,
   38,    0,    0,  124,    0,    0,   69,   71,    0,  202,
    0,    0,    0,   12,   15,    0,   13,    0,    0,   50,
   55,   51,    0,    0,    0,    0,  152,  153,  154,  155,
  156,  157,  105,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  101,  102,  103,    0,    0,    0,
    0,   62,    0,   94,    0,   95,    0,  123,  137,  139,
   25,   43,   36,    0,   35,  126,   74,   73,   72,   67,
   70,   68,    0,    0,   48,   47,   19,   59,   61,  145,
    0,  148,    0,    0,    0,    0,    0,    0,    0,  177,
    0,    0,  176,    0,    0,   64,   65,   63,   93,   92,
   90,   37,  204,    0,    0,  144,  149,    0,    0,  179,
    0,    0,  180,  178,    0,    0,  174,    0,    0,  175,
    0,    0,    0,   91,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  195,  196,    0,    0,  203,
    0,    0,  186,  187,  189,  188,  181,  182,  185,  184,
  183,    0,    0,    0,    0,    0,    0,  199,  200,  197,
  198,    0,    0,  192,  193,    0,  190,  191,  194,
};
final static short yydgoto[] = {                          3,
   19,   20,   21,   22,   23,   24,   56,  105,  106,  162,
  163,  164,  165,   25,   26,   77,   78,   79,  212,  213,
   27,   81,   28,   84,   85,  220,   29,  133,  160,  161,
  109,  174,  175,  176,  177,  244,  157,  226,  227,  228,
  289,   31,   32,   93,   33,   34,  117,  118,  134,  344,
  120,  121,  122,   35,   36,   37,   38,   39,   70,  245,
  301,  246,   40,   41,  247,  248,  249,  250,  251,  252,
   48,   49,   50,   54,  231,  324,  361,
};
final static short yysindex[] = {                      -103,
    0,   70,    0,  -70, -249, -245,   16, -241, -219,    1,
    0,    0,    0,    0,   20,   93,   99,    0,   45,    0,
    0,    0,    0,    0,    0, -214,   -3, -201,    0,    0,
    0,  321,   78,  113,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  118,    0,   44,
  138,  132,    0,  -56,    0,  -31,  -10,   13,    0,  -40,
 -202,  212,  -23,    0,  -37,  135,  247,    0,    0,  125,
  263,    0,  148,    0,    0,    0,  251,    0,  460,    0,
    0,    0,    0,    0,   -8,  254,    0,    0,    0,    0,
    0,    0,   -7,  -22,   64,    0,    0,    0,    0,    9,
  284,   76,  403,  403,    0,   11,  -97,  173,    0,  337,
    0,    0,    0,    0, -107,  -16,  -34,  404,  158,   80,
    0,    0,   92,  304,  -18,    0,    0,    0,    0,   21,
    0,    0,  -29,  158,  255,  339,    0,    0,  347,  353,
 -214,  338,   -7,   -7,   -7,   -7,   -7,  370,   63,    0,
    0,    0,  -25,    0,  144,  144,    0,   83,    0,   49,
    0,  -91,    0,    0,    0,    0, -214,    0,  236,    0,
    0, -214, -201,  -81,    0,    0,    0,    0,  242,  267,
    0,    0,    0,    0,  366,   17,   17,  298,   17,   17,
   17,   17,   17,   17,   17,   17,   17,  382,    9,  369,
  -14, -115,    7,  388,    0,    0,  315,    0,  405,    0,
   -7,    0,    0,    0,    0,    0,    0,    0, -214,  330,
    0,   -5,  416,    0,    2, -112,    0,    0,  -36,    0,
   52,   76,   76,    0,    0,   53,    0,  251,   98,    0,
    0,    0,  374,    0,  246,  270,    0,    0,    0,    0,
    0,    0,    0,  404,  404, -156,  -33,   19,  158,  158,
  158,  158,   80,   80,    0,    0,    0,  196,  424,    9,
  436,    0,  340,    0,  441,    0,  172,    0,    0,    0,
    0,    0,    0,  443,    0,    0,    0,    0,    0,    0,
    0,    0,   83,   83,    0,    0,    0,    0,    0,    0,
  376,    0,  267,  446,  267,  449,  466,  267,  486,    0,
  267,  498,    0,  485,  114,    0,    0,    0,    0,    0,
    0,    0,    0,  508,  510,    0,    0,  294,  309,    0,
  310,  311,    0,    0,  313,  316,    0,  -92,  318,    0,
  267,   83,   83,    0,   83,   83,  523,  526,  536,  540,
  542,  544,  545,  546,  559,    0,    0,  560,  524,    0,
  564,  567,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   83,   83,  267,  267,  572,  573,    0,    0,    0,
    0,  267,  307,    0,    0,   -1,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  326,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  345,
    0,    0,  439,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  364,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  589,    0,  167,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   14,  394,  161,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  211,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   25,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  183,    0,  194,  227,    0,
    0,    0,    0,  478,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   34,   59,    0,    0,    0,  421,  432,
  465,  474,  273,  351,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  615,    0,    0,    0,   56,    0,  530,    0,  537,
   29,    0,    0,  -17,  -13, -145,  502,  426,  552,    5,
  -75,    0,  -24,  476,    0,  503,   47,  557,    0,  305,
    0,  547,   33,    0,    0,   -2, -132,  497, -110,    0,
    0,    0,    0,    0,    0,    0,    0,  378,  482,    0,
  367,  301,  317,    0,    0,    0,    0,    0,  641,  451,
    0,  414,    0,    0,  539,  704,  741,  751,  759,  767,
    0,    0,    0,  653,  402, -212, -296,
};
final static int YYTABLESIZE=840;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         30,
   64,   53,  116,  131,  292,   53,  188,  115,  104,  274,
  310,  205,  290,   30,   30,  224,   30,  116,  152,    2,
  128,  236,  115,  116,  184,  156,  238,  171,  115,  108,
   55,  149,  116,  234,   57,  285,   17,  115,   59,  201,
   80,   17,  389,  240,   61,  287,  116,  276,  156,  362,
  104,  115,  116,  124,   81,   58,  116,  115,  125,   65,
  203,  115,  313,   53,   45,   76,  269,   30,  272,  132,
   30,   69,   69,   82,   82,  376,  377,   59,   83,  225,
  225,  325,  173,  173,   17,  166,  166,   97,   18,  167,
  167,  103,  232,  172,  172,  293,  297,  151,  132,   83,
  168,  168,   98,  221,  168,  168,  307,  233,  155,   17,
  294,  141,  107,   18,  148,  291,  197,   94,  291,   16,
  185,  195,   51,   59,   16,  138,  196,  115,  138,  358,
  359,  155,   17,  103,  219,  219,   18,  317,   17,   72,
  273,  298,   18,  202,  166,  168,  168,   45,  167,  173,
  225,  166,    1,  225,  173,  167,  299,  342,   95,  168,
  172,   96,   11,  353,   17,  172,  168,   16,   18,   74,
  354,  168,  343,  181,  182,  183,  168,   11,   12,   13,
   14,   99,   59,   11,   12,   13,   14,   17,   59,  100,
  235,   18,   16,   11,   12,   13,   14,  235,   59,  101,
  193,   97,  194,   97,   97,   97,  241,   23,   51,   52,
   23,  241,  321,  178,  193,   16,  194,   68,  129,   97,
   97,   16,   97,   20,  123,   23,  204,  102,  308,  309,
  223,  130,  127,  129,   58,  186,  187,  200,   11,  129,
  115,  271,  111,  112,  113,  114,  130,   16,  129,  137,
  284,   89,  130,   53,   89,  126,   62,  111,  112,  113,
  114,  130,  275,  111,  112,  113,  114,   60,  110,   89,
   16,  288,  111,  112,  113,  114,  237,   51,   59,   63,
  311,  312,  242,   81,   81,   97,  111,  112,  113,  114,
  135,   23,  111,  112,  113,  114,  111,  112,  113,  114,
    4,    5,    6,   82,   82,    7,  139,   20,    8,  141,
   18,    9,   10,   98,  150,   98,   98,   98,   58,   11,
   12,   13,   14,  158,   15,    4,    5,    6,   83,   83,
    7,   98,   98,    8,   98,   89,    9,   10,   12,   13,
   14,   18,   59,  154,   11,   12,   13,   14,   66,   15,
   18,   60,   67,    7,   66,  159,    8,  198,   71,    7,
   10,  199,    8,  112,  113,  114,   10,   11,   12,   13,
   14,  130,   15,   11,   12,   13,   14,  180,   15,  206,
   66,   92,  207,   53,  136,    7,  130,  208,    8,  243,
   79,   99,   10,   99,   99,   99,  209,   98,  211,   11,
   12,   13,   14,   66,   15,   76,  253,  140,    7,   99,
   99,    8,   99,   51,   59,   10,   97,   18,   11,   18,
  243,  268,   11,   12,   13,   14,  270,   15,  278,  243,
   97,   97,   97,   97,   84,   97,   97,   97,   97,  279,
   97,   23,   23,   23,   23,  280,   23,   11,   12,   13,
   14,  314,   59,   84,  283,   84,  286,   20,   20,   20,
   20,   87,   20,  191,  319,  192,   89,  316,   58,   58,
   58,   58,   88,   58,  230,   99,  112,  113,  114,  318,
   87,  320,   87,  322,   80,   89,   89,   89,   89,  330,
   89,   88,  333,   88,  218,  265,  266,  267,  300,   77,
  326,   60,   60,   60,   60,   85,   60,  303,  304,  334,
   11,   12,   13,   14,   86,   59,   11,   12,   13,   14,
  147,   59,   66,   79,   85,  341,   85,    7,   98,  337,
    8,  305,  306,   86,   10,   86,  295,  296,   76,  119,
   42,  340,   98,   98,   98,   98,   15,   98,   98,   98,
   98,  345,   98,  256,   42,   42,  347,   42,    7,  263,
  264,    8,  386,  254,  255,   10,  363,    7,  346,  364,
    8,  348,  349,  350,   10,  351,   86,   15,  352,  365,
  355,  130,  373,  366,  230,  367,   15,  368,  369,  370,
   87,   88,   89,   90,   91,  130,  130,  130,  130,  130,
   76,  258,  371,  372,  374,  130,   99,  375,   42,  323,
  323,   42,  382,  383,   76,   76,   76,   76,   76,  130,
   99,   99,   99,   99,  128,   99,   99,   99,   99,   66,
   99,   66,  159,   75,    7,  170,    7,    8,  257,    8,
  169,   10,  210,   10,  282,   12,   13,   14,  239,   59,
  153,  222,  229,   15,  179,   15,  302,   73,  323,  323,
   60,  360,  360,   84,   84,   84,   84,  119,  119,  315,
  259,  260,  261,  262,    0,  189,  190,   11,   12,   13,
   14,    0,   59,    0,  277,    0,    0,    0,  360,  360,
   87,   87,   87,   87,   77,  214,  215,  216,  217,    0,
    0,   88,   88,   88,   88,   43,    0,    0,   77,   77,
   77,   77,   77,    0,  327,  142,  329,    0,  332,   43,
   43,  336,   43,    0,  339,    0,    0,    0,    0,  143,
  144,  145,  146,   76,   85,   85,   85,   85,    0,    0,
    0,    0,   44,   86,   86,   86,   86,   76,   76,   76,
   76,   76,   45,  328,  357,  331,   44,   44,  335,   44,
   46,  338,  281,    0,    0,    0,   45,   45,   47,   45,
    0,    0,    0,   43,   46,   46,   43,   46,    0,    0,
    0,    0,   47,   47,    0,   47,    0,  379,  381,    0,
    0,  356,    0,    0,    0,  385,  388,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   44,    0,    0,   44,    0,    0,    0,    0,    0,    0,
   45,    0,    0,   45,  378,  380,    0,    0,   46,    0,
    0,   46,  384,  387,    0,    0,   47,    0,    0,   47,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          2,
    0,    4,   40,   41,   41,    8,   41,   45,   40,  125,
   44,   41,  125,   16,   17,   41,   19,   40,   41,  123,
   44,  167,   45,   40,   41,   40,  172,  125,   45,   40,
  280,   40,   40,  125,  280,   41,   40,   45,  280,   58,
   44,   40,   44,  125,  264,   44,   40,   41,   40,  346,
   40,   45,   40,  256,   41,   40,   40,   45,   61,   40,
   40,   45,   44,   66,   40,  280,  199,   70,  201,   65,
   73,   16,   17,   27,   41,  372,  373,  280,  280,  155,
  156,  294,  107,  108,   40,  103,  104,   44,   44,  103,
  104,  123,   44,  107,  108,   44,   44,   93,   94,   41,
  103,  104,   59,   41,  107,  108,  263,   59,  123,   40,
   59,   59,  123,   44,  123,  226,   37,   40,  229,  123,
  116,   42,  279,  280,  123,   70,   47,   45,   73,  342,
  343,  123,   40,  123,  148,  149,   44,  270,   40,   41,
  256,   44,   44,  123,  162,  148,  149,  123,  162,  174,
  226,  169,  256,  229,  179,  169,   59,   44,   46,  162,
  174,   44,  275,  256,   40,  179,  169,  123,   44,  125,
  263,  174,   59,  281,  282,  283,  179,  275,  276,  277,
  278,   44,  280,  275,  276,  277,  278,   40,  280,   58,
  162,   44,  123,  275,  276,  277,  278,  169,  280,  256,
   43,   41,   45,   43,   44,   45,  174,   41,  279,  280,
   44,  179,   41,   41,   43,  123,   45,  125,  256,   59,
   60,  123,   62,   41,  265,   59,  256,  259,  262,  263,
  256,  269,  256,  256,   41,  270,  271,  256,  275,  256,
   45,  256,  280,  281,  282,  283,  269,  123,  256,  125,
  256,   41,  269,  256,   44,   44,  256,  280,  281,  282,
  283,  269,  256,  280,  281,  282,  283,   41,  256,   59,
  123,  225,  280,  281,  282,  283,   41,  279,  280,  279,
  262,  263,   41,  270,  271,  125,  280,  281,  282,  283,
   44,  125,  280,  281,  282,  283,  280,  281,  282,  283,
  256,  257,  258,  270,  271,  261,   44,  125,  264,   59,
   44,  267,  268,   41,   61,   43,   44,   45,  125,  275,
  276,  277,  278,   40,  280,  256,  257,  258,  270,  271,
  261,   59,   60,  264,   62,  125,  267,  268,  276,  277,
  278,   44,  280,  280,  275,  276,  277,  278,  256,  280,
   44,  125,  260,  261,  256,  280,  264,  266,  260,  261,
  268,   58,  264,  281,  282,  283,  268,  275,  276,  277,
  278,   46,  280,  275,  276,  277,  278,   41,  280,  125,
  256,   61,   44,  386,  260,  261,   61,   41,  264,  123,
   46,   41,  268,   43,   44,   45,   44,  125,   61,  275,
  276,  277,  278,  256,  280,   61,   41,  260,  261,   59,
   60,  264,   62,  279,  280,  268,  256,   44,  275,   44,
  123,   40,  275,  276,  277,  278,   58,  280,   41,  123,
  270,  271,  272,  273,   41,  275,  276,  277,  278,  125,
  280,  275,  276,  277,  278,   41,  280,  275,  276,  277,
  278,  256,  280,   60,  125,   62,   41,  275,  276,  277,
  278,   41,  280,   60,  125,   62,  256,   44,  275,  276,
  277,  278,   41,  280,  158,  125,  281,  282,  283,   44,
   60,   41,   62,   41,   46,  275,  276,  277,  278,   44,
  280,   60,   44,   62,  125,  195,  196,  197,  125,   61,
  125,  275,  276,  277,  278,   41,  280,  262,  263,   44,
  275,  276,  277,  278,   41,  280,  275,  276,  277,  278,
   61,  280,  256,   46,   60,   41,   62,  261,  256,   44,
  264,  262,  263,   60,  268,   62,  232,  233,   61,   58,
    2,   44,  270,  271,  272,  273,  280,  275,  276,  277,
  278,   44,  280,  256,   16,   17,  263,   19,  261,  193,
  194,  264,  256,  186,  187,  268,   44,  261,   59,   44,
  264,  263,  263,  263,  268,  263,  256,  280,  263,   44,
  263,  256,   59,   44,  268,   44,  280,   44,   44,   44,
  270,  271,  272,  273,  274,  270,  271,  272,  273,  274,
  256,  188,   44,   44,   41,  280,  256,   41,   70,  293,
  294,   73,   41,   41,  270,  271,  272,  273,  274,  256,
  270,  271,  272,  273,  280,  275,  276,  277,  278,  256,
  280,  256,   44,   19,  261,  106,  261,  264,  188,  264,
  104,  268,  141,  268,  219,  276,  277,  278,  173,  280,
   94,  149,  156,  280,  108,  280,  243,   17,  342,  343,
    8,  345,  346,  270,  271,  272,  273,  186,  187,  268,
  189,  190,  191,  192,   -1,  272,  273,  275,  276,  277,
  278,   -1,  280,   -1,  203,   -1,   -1,   -1,  372,  373,
  270,  271,  272,  273,  256,  144,  145,  146,  147,   -1,
   -1,  270,  271,  272,  273,    2,   -1,   -1,  270,  271,
  272,  273,  274,   -1,  301,  256,  303,   -1,  305,   16,
   17,  308,   19,   -1,  311,   -1,   -1,   -1,   -1,  270,
  271,  272,  273,  256,  270,  271,  272,  273,   -1,   -1,
   -1,   -1,    2,  270,  271,  272,  273,  270,  271,  272,
  273,  274,    2,  303,  341,  305,   16,   17,  308,   19,
    2,  311,  211,   -1,   -1,   -1,   16,   17,    2,   19,
   -1,   -1,   -1,   70,   16,   17,   73,   19,   -1,   -1,
   -1,   -1,   16,   17,   -1,   19,   -1,  374,  375,   -1,
   -1,  341,   -1,   -1,   -1,  382,  383,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   70,   -1,   -1,   73,   -1,   -1,   -1,   -1,   -1,   -1,
   70,   -1,   -1,   73,  374,  375,   -1,   -1,   70,   -1,
   -1,   73,  382,  383,   -1,   -1,   70,   -1,   -1,   73,
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
"executable_statement : if_then_statement",
"executable_statement : if_then_else_statement",
"executable_statement : for_in_range_statement",
"executable_statement : print_statement",
"executable_statement : expression_statement",
"executable_statement : empty_statement",
"local_variable_declaration_statement : local_variable_declaration ','",
"local_variable_declaration : type variable_declarators",
"statement : statement_without_trailing_substatement",
"statement : if_then_statement",
"statement : if_then_else_statement",
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
"if_then_statement : IF '(' equality_expression ')' executable_block END_IF ','",
"if_then_statement : IF '(' equality_expression ')' executable_statement END_IF ','",
"if_then_statement : IF '(' equality_expression ')' executable_statement ','",
"if_then_statement : IF '(' equality_expression ')' executable_block ','",
"if_then_statement : IF '(' equality_expression ')' error END_IF ','",
"if_then_statement : IF '(' error ')' executable_block END_IF ','",
"if_then_statement : IF '(' error ')' executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_block error ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statement ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statement ELSE executable_block END_IF ','",
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

//#line 434 "grammer.y"

private static AnalizadorLexico aLexico;
private static PolacaInversa pInversa;
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
    pInversa = new PolacaInversa();
    scope = new Scope();
    
    aSintactico.run();

    //aSintactico.dump_stacks(yylval_recognition);
    System.out.println(Logger.dumpLog());

    if(!Logger.errorsOcurred()){
      System.out.println("No se produjieron errores."); //Para la parte 4, generacion de codigo maquina
    }

    pInversa.printRules();
    System.out.println(aLexico.getProgram());

}


//#line 959 "Parser.java"
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
{Logger.logRule(aLexico.getProgramPosition(), "No se reconocio el programa.");}
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
case 24:
//#line 91 "grammer.y"
{pInversa.add(val_peek(2).sval, "=");}
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
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
break;
case 78:
//#line 206 "grammer.y"
{pInversa.add(".", val_peek(0).sval);}
break;
case 82:
//#line 214 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 83:
//#line 215 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 85:
//#line 219 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 86:
//#line 220 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 87:
//#line 221 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 88:
//#line 222 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 90:
//#line 226 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
break;
case 91:
//#line 226 "grammer.y"
{pInversa.add(val_peek(4).sval);}
break;
case 92:
//#line 227 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 93:
//#line 228 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 94:
//#line 229 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 95:
//#line 230 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 96:
//#line 231 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No es una expresion aritmetica valida.");}
break;
case 97:
//#line 234 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 98:
//#line 235 "grammer.y"
{pInversa.add("+");}
break;
case 99:
//#line 236 "grammer.y"
{pInversa.add("-");}
break;
case 101:
//#line 240 "grammer.y"
{pInversa.add("*");}
break;
case 102:
//#line 241 "grammer.y"
{pInversa.add("/");}
break;
case 103:
//#line 242 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
break;
case 106:
//#line 247 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 107:
//#line 248 "grammer.y"
{pInversa.add(val_peek(0).sval);}
break;
case 108:
//#line 251 "grammer.y"
{pInversa.add(val_peek(0).sval);}
break;
case 109:
//#line 252 "grammer.y"
{pInversa.add(val_peek(0).sval);}
break;
case 110:
//#line 253 "grammer.y"
{yyval = new ParserVal(chequearRangoLong(val_peek(0).sval)); pInversa.add(val_peek(0).sval);}
break;
case 111:
//#line 254 "grammer.y"
{yyval = new ParserVal(negarDouble(val_peek(0).sval)); pInversa.add(val_peek(1).sval);}
break;
case 112:
//#line 255 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval)); pInversa.add(val_peek(1).sval);}
break;
case 113:
//#line 256 "grammer.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 114:
//#line 261 "grammer.y"
{pInversa.add("=");}
break;
case 115:
//#line 262 "grammer.y"
{pInversa.add("-=");}
break;
case 116:
//#line 263 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 117:
//#line 264 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 118:
//#line 265 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 119:
//#line 266 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 120:
//#line 267 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 121:
//#line 270 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 122:
//#line 271 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 123:
//#line 272 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 124:
//#line 273 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 125:
//#line 274 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 126:
//#line 275 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 130:
//#line 290 "grammer.y"
{pInversa.add(val_peek(0).sval); scope.changeScope(val_peek(0).sval);}
break;
case 138:
//#line 314 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 139:
//#line 315 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 141:
//#line 317 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 142:
//#line 318 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 143:
//#line 319 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 158:
//#line 347 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 170:
//#line 368 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 174:
//#line 379 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 175:
//#line 380 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 176:
//#line 381 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 177:
//#line 382 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 178:
//#line 383 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
break;
case 179:
//#line 384 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 180:
//#line 385 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 181:
//#line 389 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 182:
//#line 390 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 183:
//#line 391 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 184:
//#line 392 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 185:
//#line 393 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");}
break;
case 186:
//#line 394 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 187:
//#line 395 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 188:
//#line 396 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 189:
//#line 397 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 190:
//#line 401 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 191:
//#line 402 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 192:
//#line 403 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 193:
//#line 404 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 194:
//#line 405 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 195:
//#line 406 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 196:
//#line 407 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 197:
//#line 408 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 198:
//#line 409 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 199:
//#line 410 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 200:
//#line 411 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 205:
//#line 427 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 206:
//#line 428 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 207:
//#line 429 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 208:
//#line 430 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
case 209:
//#line 431 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1576 "Parser.java"
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
