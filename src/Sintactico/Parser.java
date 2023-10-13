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
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
import Tools.Tupla;
import Tools.TablaSimbolos;
import java.io.IOException;
//#line 27 "Parser.java"




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
    7,    9,    9,   10,   11,   11,   12,   15,   15,   16,
   16,   17,   18,   13,   20,   22,   23,   23,   23,   23,
   23,   21,   21,   24,   26,    8,   27,   27,    4,   29,
   29,   29,   29,   30,   30,   31,   31,   32,   33,    5,
    5,    5,    5,    5,   35,   35,   36,   36,   37,   38,
   39,   39,   40,   41,   41,   43,   44,   44,   45,   45,
   45,   46,   46,   46,   46,   46,   19,   19,   19,   47,
   47,   47,   48,   48,   48,   48,   49,   49,   49,   49,
   50,   50,   50,   50,   50,   50,   42,   42,   51,   51,
   51,   51,   51,   51,   14,   14,   52,   34,   53,   53,
   54,   54,   55,   28,   25,   25,   25,   25,   25,   25,
   25,   57,   57,   56,   56,   58,   58,    6,    6,   59,
   59,   59,   59,   59,   59,   60,   68,   61,   61,   61,
   61,   61,   61,   69,   69,   69,   66,   66,   70,   70,
   67,   62,   62,   62,   62,   62,   62,   62,   63,   63,
   63,   63,   63,   63,   63,   63,   63,   64,   64,   64,
   64,   64,   71,   72,   74,   73,   65,   65,   65,   65,
};
final static short yylen[] = {                            2,
    1,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    3,    1,    2,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    2,    1,    4,    4,    6,    3,
    3,    1,    1,    2,    1,    2,    1,    3,    3,    3,
    3,    2,    2,    1,    2,    1,    1,    2,    3,    5,
    6,    6,    6,    3,    3,    3,    1,    2,    1,    2,
    1,    1,    3,    1,    1,    3,    1,    1,    1,    3,
    3,    1,    3,    3,    3,    3,    1,    4,    4,    1,
    3,    3,    1,    3,    3,    3,    1,    3,    2,    1,
    1,    1,    1,    2,    2,    2,    1,    1,    4,    3,
    6,    4,    3,    6,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    5,    3,    5,    4,    4,    2,
    2,    3,    2,    1,    2,    1,    2,    1,    1,    1,
    1,    1,    1,    1,    1,    2,    2,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    2,    2,    1,    1,
    1,    7,    7,    6,    6,    7,    7,    7,    9,    9,
    9,    9,    9,    9,    9,    9,    9,   12,   12,   13,
    8,    8,    1,    1,    1,    1,    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   26,  112,  111,
  113,    0,    0,  151,    0,    0,    0,    3,    5,    6,
    7,    8,  142,    0,    0,    0,  144,    0,  149,    0,
    0,    0,  150,  105,  107,  109,  110,  128,  129,  139,
  140,  141,  143,  146,  145,    0,  138,    0,    0,    0,
    0,    0,    0,  108,  173,    0,    0,    0,  180,    0,
    0,    0,  120,  124,    0,    0,  121,    0,    0,    4,
   22,    0,   18,    0,   33,   24,   32,    0,   25,   98,
   97,    0,    0,    0,  136,  147,  148,  179,    0,    0,
   54,    0,    0,    9,    0,    0,    0,   39,    0,   90,
   91,   92,   93,    0,    0,    0,    0,    0,    0,   83,
   87,    0,    0,    0,  178,  177,    0,  100,   35,    0,
    0,    0,    0,  116,  125,    0,    0,    0,    0,    0,
    0,   63,  103,    0,   66,    0,    0,   57,   59,    0,
  114,    0,   37,    0,   12,   14,   15,   16,    0,  106,
   10,   42,    0,    0,    0,   44,   46,   47,   43,    0,
    0,   94,   96,   95,   89,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   99,  118,    0,  119,    0,   19,
   21,   23,   31,    0,    0,   30,    0,    0,  102,   62,
   61,   60,   55,   58,   56,    0,   11,   13,    0,    0,
    0,   40,   45,   41,    0,    0,    0,    0,  130,  131,
  132,  133,  134,  135,   88,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   84,   85,   86,    0,
    0,    0,    0,   50,    0,    0,    0,  115,  117,   34,
   28,    0,   27,    0,   38,   17,   49,  123,    0,  126,
    0,    0,    0,    0,    0,    0,    0,  155,    0,    0,
  154,    0,  174,    0,   52,   53,   51,   79,   78,  101,
    0,  104,  122,  127,    0,    0,  157,    0,    0,  158,
  156,    0,    0,  152,    0,    0,  153,    0,    0,   29,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  171,
  172,  176,    0,  164,  165,  167,  166,  159,  160,  163,
  162,  161,    0,  175,    0,    0,    0,  168,  169,  170,
};
final static short yydgoto[] = {                         16,
   17,   18,   19,   20,   21,   22,   94,   95,  144,  145,
  146,  147,   23,   24,   72,   73,   74,  191,  119,   25,
   76,   26,   79,  195,   27,  120,  142,  143,   98,  155,
  156,  157,  158,  216,   91,  137,  138,  139,  202,   29,
   30,   82,   31,   32,  106,  107,  121,  109,  110,  111,
   33,   34,   35,   36,   37,   65,  217,  259,  218,   38,
   39,  219,  220,  221,  222,  223,  224,   46,   47,   48,
   56,  274,  313,  325,
};
final static short yysindex[] = {                        25,
   12, -239, -230,   33, -193, -157,    1,    0,    0,    0,
    0,   85,   73,    0,   79,    0,   50,    0,    0,    0,
    0,    0,    0, -151,   -7, -142,    0,    0,    0,   36,
  107,  111,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  117,    0,   55,  123,  -34,
 -107,  -14,  -17,    0,    0,  -97, -188,  -20,    0,  -38,
 -104,  132,    0,    0,  102,  134,    0,  130,   12,    0,
    0,  122,    0,  118,    0,    0,    0,    5,    0,    0,
    0,  -13,  -32,  -96,    0,    0,    0,    0,  -89,  -89,
    0,  -87,  266,    0,   76,  -86,  136,    0,  156,    0,
    0,    0,    0,  202,  -23,  -16,  120,   98,  103,    0,
    0,  -66,  151,  -44,    0,    0,  171,    0,    0,   16,
   98,   88,  180,    0,    0,  174,  197, -151,  -13,  -70,
  147,    0,    0,   18,    0,   -2, -113,    0,    0,  -37,
    0,  257,    0,  236,    0,    0,    0,    0, -151,    0,
    0,    0, -151, -142,  301,    0,    0,    0,    0,  142,
  208,    0,    0,    0,    0,  220,   -5,   -5,  265,   -5,
   -5,   -5,   -5,   -5,   -5,   -5,   -5,   -5,  283,  -34,
  274,  -30,   -9,   82,    0,    0,  227,    0,  334,    0,
    0,    0,    0, -151,  262,    0,   20,  133,    0,    0,
    0,    0,    0,    0,    0,  -87,    0,    0,   74,  122,
  351,    0,    0,    0,  235,    0, -243,  -98,    0,    0,
    0,    0,    0,    0,    0,  120,  120, -217,  -33,  141,
   98,   98,   98,   98,  103,  103,    0,    0,    0,   86,
  353,  -34,  357,    0,  368,  110,  380,    0,    0,    0,
    0,  185,    0,  401,    0,    0,    0,    0,  271,    0,
  208,  410,  208,  418,  419,  208,  423,    0,  208,  426,
    0,  414,    0,  400,    0,    0,    0,    0,    0,    0,
  433,    0,    0,    0,  216,  223,    0,  226,  229,    0,
    0,  231,  232,    0, -158,  234,    0,  208,   63,    0,
  446,  457,  463,  465,  473,  475,  480,  481,  484,    0,
    0,    0,  471,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   63,    0,  490,  292,  -39,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
  534,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -46,    0,    0,    0,    0,  537,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   10,    0,    0,
  -12,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  503,    0,   19,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   17,  398,  160,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   44,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  434,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   96,
    0,    0,    0,    0,    0,   30,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   42,   94,    0,    0,    0,
  406,  411,  415,  420,  173,  340,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  532,    0,    0,    0,   71,  464,    0,    0,  421,
    0,    0,  -64,  -49,  -53,  438,  374,    0,  352,  -36,
    0,  -45,  428,  443,   -4,  492,    0,  377,    0,  483,
   -6,    0,    0,  493, -108,  494,  -73,    0,    0,    0,
    0,    0,    0,    0,    0,    4,  425,  254,  386, -197,
    0,    0,    0,    0,    0,  570, -139,    0, -103,    0,
    0,  487,  505,  523,  554,  587,  629,    0,    0,    0,
    0,    0,    0,    0,
};
final static int YYTABLESIZE=697;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        108,
   59,  105,  118,  205,  330,   90,  104,  105,  133,   90,
  268,  203,  104,  182,  108,   93,  105,  165,  261,  262,
   77,  104,  105,  116,  169,   97,  105,  104,  148,  229,
  105,  104,   15,   68,  105,  104,   75,   15,  152,  104,
   51,  200,  273,  149,  131,  265,  153,  153,   65,   52,
  154,  154,  136,  136,  193,   67,  185,   69,  199,   20,
  253,   49,   20,  204,   15,  230,  204,  113,   14,   50,
   64,  241,   53,  244,  184,   67,  198,   20,  252,  148,
  194,  194,   70,   64,   77,   64,   54,   77,   89,   15,
   64,   54,   89,   14,  149,  209,   81,  307,   86,  210,
  136,  312,   77,  136,  308,  153,   57,  104,   96,  154,
  153,  260,   15,   87,  154,   13,   14,  256,   15,   67,
   13,  285,   14,  288,   60,  324,  292,  130,   71,  295,
  104,  201,  128,  276,   71,  125,   48,   78,  125,  178,
  174,   15,  175,   20,  176,   14,   83,   13,  213,  177,
  279,   92,  174,  213,  175,  284,   84,  286,  310,  289,
   85,    8,  293,  263,  264,  296,   88,  112,   77,   15,
  226,  227,   13,   14,   49,  122,  159,  126,  129,  172,
  128,  173,  214,  135,  271,    8,  328,  196,    8,    9,
   10,   11,  141,   54,  311,   13,  161,   63,   93,  179,
   80,   13,   80,   80,   80,    9,   10,   11,  180,   54,
  183,  181,  186,   81,  188,   81,   81,   81,   80,   80,
   48,   80,  329,  187,   13,  243,  124,  108,  266,  267,
  117,   81,   81,  108,   81,  115,  117,    8,   99,   49,
  189,  100,  101,  102,  103,  117,  245,  100,  101,  102,
  103,   14,   13,  167,  168,  117,  100,  101,  102,  103,
  225,   65,  100,  101,  102,  103,  100,  101,  102,  103,
  100,  101,  102,  103,  100,  101,  102,  103,   14,   58,
    1,    2,    3,   64,   80,    4,   69,   69,    5,  106,
   49,    6,    7,   20,   20,   20,   20,   81,   20,    8,
    9,   10,   11,   64,   12,   69,    2,    3,   14,   80,
    4,   70,   70,    5,   14,  206,    6,    7,   77,   77,
   77,   77,  240,   77,    8,    9,   10,   11,   61,   12,
  215,  242,   62,    4,   61,   14,    5,  247,   66,    4,
    7,  272,    5,  101,  102,  103,    7,    8,    9,   10,
   11,  248,   12,    8,    9,   10,   11,   61,   12,  258,
  207,  123,    4,   71,   71,    5,  101,  102,  103,    7,
   48,   48,   48,   48,  249,   48,    8,    9,   10,   11,
   82,   12,   82,   82,   82,   61,  251,  215,  254,  127,
    4,  170,  171,    5,  257,  283,  275,    7,   82,   82,
  277,   82,  269,  270,    8,    9,   10,   11,  278,   12,
    8,    9,   10,   11,  215,   54,    8,    9,   10,   11,
  280,   54,    9,   10,   11,  212,   54,  235,  236,   80,
   80,   80,   80,  132,   80,   80,   80,   80,   72,   80,
  281,  282,   81,   81,   81,   81,   75,   81,   81,   81,
   81,   76,   81,  287,  298,   73,  166,   72,  299,   72,
   74,  290,  291,   61,   82,   75,  294,   75,    4,  297,
   76,    5,   76,  300,   73,    7,   73,  108,  301,   74,
  192,   74,  162,  163,  164,  302,   40,   12,  303,  314,
   61,  304,   28,  305,  306,    4,  309,   55,    5,   40,
  315,   40,    7,   40,   41,   28,  316,   28,  317,   28,
    8,    9,   10,   11,   12,   54,  318,   41,  319,   41,
  228,   41,   42,  320,  321,    4,   61,  322,    5,  323,
  326,    4,    7,    2,    5,   42,    1,   42,    7,   42,
    8,    9,   10,   11,   12,   54,  137,  327,   70,  114,
   12,   40,    4,   43,   40,    5,   36,   28,  151,    7,
   28,  237,  238,  239,  208,  190,   43,  250,   43,   41,
   43,   12,   41,  197,  134,    8,    9,   10,   11,  160,
   54,  211,  255,  140,   68,  150,   44,   42,  150,  150,
   42,  108,  108,    0,  231,  232,  233,  234,    0,   44,
    0,   44,    0,   44,    0,    0,    0,  246,    0,   82,
   82,   82,   82,    0,   82,   82,   82,   82,   43,   82,
    0,   43,  150,  150,    0,    0,    0,    0,   45,    0,
    0,    0,    0,    0,    0,    0,  150,    0,    0,    0,
    0,   45,    0,   45,    0,   45,    0,  150,    0,    0,
    0,   44,  150,    0,   44,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   72,   72,   72,
   72,    0,    0,    0,    0,   75,   75,   75,   75,    0,
   76,   76,   76,   76,   73,   73,   73,   73,    0,   74,
   74,   74,   74,   45,    0,    0,   45,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         46,
    0,   40,   41,   41,   44,   40,   45,   40,   41,   40,
   44,  125,   45,   58,   61,  123,   40,   41,  262,  263,
   25,   45,   40,   44,   41,   40,   40,   45,   93,  169,
   40,   45,   40,   46,   40,   45,   44,   40,  125,   45,
  280,   44,  240,   93,   40,  263,   96,   97,   61,  280,
   96,   97,   89,   90,  125,   46,   41,   41,   41,   41,
   41,  279,   44,  137,   40,  169,  140,  256,   44,   58,
   61,  180,   40,  182,   59,   46,   59,   59,   59,  144,
  130,  131,   41,   13,   41,   15,  280,   44,  123,   40,
   61,  280,  123,   44,  144,  149,   61,  256,   44,  153,
  137,  299,   59,  140,  263,  155,  264,   45,  123,  155,
  160,  215,   40,   59,  160,  123,   44,   44,   40,   41,
  123,  261,   44,  263,   40,  323,  266,  123,  280,  269,
   45,  136,   59,  242,   41,   65,   41,  280,   68,   37,
   43,   40,   45,  125,   42,   44,   40,  123,  155,   47,
   41,  259,   43,  160,   45,  259,   46,  261,  298,  263,
   44,  275,  266,  262,  263,  269,   44,  265,  125,   40,
  167,  168,  123,   44,  279,   44,   41,   44,   61,   60,
   59,   62,   41,  280,   44,  275,  326,   41,  275,  276,
  277,  278,  280,  280,  298,  123,   41,  125,  123,  266,
   41,  123,   43,   44,   45,  276,  277,  278,   58,  280,
   40,  256,  125,   41,   41,   43,   44,   45,   59,   60,
  125,   62,  326,   44,  123,  256,  125,  274,  262,  263,
  269,   59,   60,  280,   62,  256,  269,  275,  256,  279,
   44,  280,  281,  282,  283,  269,  256,  280,  281,  282,
  283,   44,  123,  270,  271,  269,  280,  281,  282,  283,
   41,  274,  280,  281,  282,  283,  280,  281,  282,  283,
  280,  281,  282,  283,  280,  281,  282,  283,   44,  279,
  256,  257,  258,  274,  125,  261,  270,  271,  264,  280,
  279,  267,  268,  275,  276,  277,  278,  125,  280,  275,
  276,  277,  278,  274,  280,  256,  257,  258,   44,  274,
  261,  270,  271,  264,   44,   59,  267,  268,  275,  276,
  277,  278,   40,  280,  275,  276,  277,  278,  256,  280,
  123,   58,  260,  261,  256,   44,  264,  256,  260,  261,
  268,  256,  264,  281,  282,  283,  268,  275,  276,  277,
  278,  125,  280,  275,  276,  277,  278,  256,  280,  125,
  125,  260,  261,  270,  271,  264,  281,  282,  283,  268,
  275,  276,  277,  278,   41,  280,  275,  276,  277,  278,
   41,  280,   43,   44,   45,  256,  125,  123,  256,  260,
  261,  272,  273,  264,   44,  125,   44,  268,   59,   60,
   44,   62,  262,  263,  275,  276,  277,  278,   41,  280,
  275,  276,  277,  278,  123,  280,  275,  276,  277,  278,
   41,  280,  276,  277,  278,  125,  280,  174,  175,  270,
  271,  272,  273,   82,  275,  276,  277,  278,   41,  280,
  256,   41,  270,  271,  272,  273,   41,  275,  276,  277,
  278,   41,  280,   44,   41,   41,  105,   60,   59,   62,
   41,   44,   44,  256,  125,   60,   44,   62,  261,   44,
   60,  264,   62,   41,   60,  268,   62,   53,  263,   60,
  129,   62,  281,  282,  283,  263,    0,  280,  263,   44,
  256,  263,    0,  263,  263,  261,  263,    5,  264,   13,
   44,   15,  268,   17,    0,   13,   44,   15,   44,   17,
  275,  276,  277,  278,  280,  280,   44,   13,   44,   15,
  256,   17,    0,   44,   44,  261,  256,   44,  264,   59,
   41,  261,  268,    0,  264,   13,    0,   15,  268,   17,
  275,  276,  277,  278,  280,  280,   44,  256,   17,   57,
  280,   65,  261,    0,   68,  264,  123,   65,   95,  268,
   68,  176,  177,  178,  144,  128,   13,  194,   15,   65,
   17,  280,   68,  131,   83,  275,  276,  277,  278,   97,
  280,  154,  206,   90,   15,   93,    0,   65,   96,   97,
   68,  167,  168,   -1,  170,  171,  172,  173,   -1,   13,
   -1,   15,   -1,   17,   -1,   -1,   -1,  183,   -1,  270,
  271,  272,  273,   -1,  275,  276,  277,  278,   65,  280,
   -1,   68,  130,  131,   -1,   -1,   -1,   -1,    0,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  144,   -1,   -1,   -1,
   -1,   13,   -1,   15,   -1,   17,   -1,  155,   -1,   -1,
   -1,   65,  160,   -1,   68,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  270,  271,  272,
  273,   -1,   -1,   -1,   -1,  270,  271,  272,  273,   -1,
  270,  271,  272,  273,  270,  271,  272,  273,   -1,  270,
  271,  272,  273,   65,   -1,   -1,   68,
};
}
final static short YYFINAL=16;
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
"program : type_declarations",
"program : error",
"type_declarations : type_declaration",
"type_declarations : type_declarations type_declaration",
"type_declaration : class_declaration",
"type_declaration : interface_declaration",
"type_declaration : implement_for_declaration",
"type_declaration : block_statement",
"class_declaration : CLASS ID class_body",
"class_declaration : CLASS ID interfaces class_body",
"class_body : '{' class_body_declarations '}'",
"class_body_declarations : class_body_declaration",
"class_body_declarations : class_body_declarations class_body_declaration",
"class_body_declaration : class_member_declaration",
"class_member_declaration : field_declaration",
"class_member_declaration : method_declaration",
"field_declaration : type variable_declarators ','",
"variable_declarators : variable_declarator",
"variable_declarators : variable_declarators ';' variable_declarator",
"variable_declarator : variable_declarator_id",
"variable_declarator : variable_declarator_id '=' variable_initializer",
"variable_declarator_id : ID",
"variable_initializer : arithmetic_operation",
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"result_type : VOID",
"method_declarator : ID '(' formal_parameter ')'",
"method_declarator : ID '{' formal_parameter '}'",
"method_declarator : ID '(' formal_parameter ';' error ')'",
"method_declarator : ID '(' ')'",
"method_declarator : ID '{' '}'",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : arithmetic_operation",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : type_name",
"interface_type_list : interface_type_list ';' type_name",
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
"implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_type ':' error ','",
"implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
"implement_for_declaration : IMPL FOR reference_type error ':' implement_for_body",
"implement_for_declaration : error ':' implement_for_body",
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
"method_invocation : ID '(' real_parameter ')'",
"method_invocation : ID '(' ')'",
"method_invocation : ID '(' real_parameter ';' error ')'",
"method_invocation : field_acces '(' real_parameter ')'",
"method_invocation : field_acces '(' ')'",
"method_invocation : field_acces '(' real_parameter ';' error ')'",
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
"executable_block_statements : executable_statament",
"executable_block_statements : executable_block_statements executable_statament",
"block_statement : local_variable_declaration_statement",
"block_statement : statement",
"executable_statament : if_then_statement",
"executable_statament : if_then_else_statement",
"executable_statament : for_in_range_statement",
"executable_statament : print_statement",
"executable_statament : expression_statement",
"executable_statament : empty_statement",
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
"if_then_statement : IF '(' equality_expression ')' executable_statament END_IF ','",
"if_then_statement : IF '(' equality_expression ')' executable_statament ','",
"if_then_statement : IF '(' equality_expression ')' executable_block ','",
"if_then_statement : IF '(' equality_expression ')' error END_IF ','",
"if_then_statement : IF '(' error ')' executable_block END_IF ','",
"if_then_statement : IF '(' error ')' executable_statament END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statament ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statament ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statament ELSE executable_block error ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_block END_IF ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statament",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statament",
"for_variable : reference_type",
"for_init : factor",
"for_update : factor",
"for_end : factor",
"print_statement : PRINT CADENA ','",
"print_statement : PRINT CADENA error",
"print_statement : error CADENA ','",
"print_statement : PRINT '\\000'",
};

//#line 397 "grammer.y"

private static AnalizadorLexico aLexico;
private static int yylval_recognition = 0;
public static boolean error = false;

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
    System.out.println("Error en el parser: " + msg);
}

// ###############################################################
// metodos auxiliares a la gramatica
// ###############################################################

private String negarDouble(String lexema) {
      
    double RDN_MIN = -2.2250738585072014D * Math.pow(10, 308);
    double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

    System.out.println("Numero dentro de negar doble: " + lexema);

    double number = 0.0;
    String n_lexema;

    try {
        number = -Double.parseDouble(lexema);
    } catch (Exception ex) {}

    System.out.println("Numero dentro de negar doble: " + number);

    if (number > RDN_MAX || number < RDN_MIN){
      Logger.logWarning(aLexico.getProgramPosition(), "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      n_lexema = String.valueOf(RDN_MAX);
    } 

    n_lexema = String.valueOf(number);

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

private String ChequeoRangoEntero(String lexema){

    long RDN_MAX = (long) Math.pow(2, 31);
    long number = 0;

    try {
        number = Long.parseLong(lexema);
    } catch (Exception ex) {}

    if (number >= RDN_MAX){
      Logger.logWarning(aLexico.getProgramPosition(), "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      TablaSimbolos.decreaseCounter(lexema);
      lexema = String.valueOf(RDN_MAX - 1);
      TablaSimbolos.addContador(lexema);
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
  ArrayList<String> out = new ArrayList<>();

  // Verifica si es un directorio o archivo válido
  if (element.isDirectory() || element.isFile()) {
    // Lista de archivos y directorios en el directorio actual
    File[] filesAndDirs = element.listFiles();

    // Itera a través de los archivos y directorios
    int i = 0;
    for (File fileOrDir : filesAndDirs) {
      String name = fileOrDir.getName();
      System.out.println("[" + i + "]" + ": " + name);
      out.add(name);
      i++;
    }
  } else {
    System.err.println("No es un directorio válido.");
  }

  return out;
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
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }


    Parser aSintactico = new Parser();
    aSintactico.run();
    //aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
    System.out.println(aLexico.getProgram());
}


//#line 869 "Parser.java"
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
//#line 32 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");}
break;
case 2:
//#line 33 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "No se reconocio el programa.");}
break;
case 9:
//#line 51 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
break;
case 10:
//#line 52 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
break;
case 17:
//#line 69 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
break;
case 27:
//#line 95 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 28:
//#line 96 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 29:
//#line 97 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 30:
//#line 98 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 31:
//#line 99 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 39:
//#line 121 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 41:
//#line 125 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 43:
//#line 127 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 50:
//#line 144 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 51:
//#line 145 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 52:
//#line 146 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 53:
//#line 147 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 54:
//#line 148 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 56:
//#line 152 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 62:
//#line 166 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 63:
//#line 174 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
break;
case 70:
//#line 189 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 71:
//#line 190 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 77:
//#line 200 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 89:
//#line 218 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 93:
//#line 226 "grammer.y"
{yyval = new ParserVal(ChequeoRangoEntero(val_peek(0).sval));}
break;
case 94:
//#line 227 "grammer.y"
{System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval); yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 95:
//#line 228 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 96:
//#line 229 "grammer.y"
{Logger.logWarning(aLexico.getProgramPosition() ,"Los tipos enteros deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 99:
//#line 238 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 100:
//#line 239 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 101:
//#line 240 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 102:
//#line 241 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 103:
//#line 242 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 104:
//#line 243 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 116:
//#line 283 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 117:
//#line 284 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 119:
//#line 286 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 120:
//#line 287 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 121:
//#line 288 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 136:
//#line 316 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 148:
//#line 337 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 152:
//#line 348 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 153:
//#line 349 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 154:
//#line 350 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 155:
//#line 351 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 156:
//#line 352 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
break;
case 157:
//#line 353 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 158:
//#line 354 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 159:
//#line 358 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 160:
//#line 359 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 161:
//#line 360 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 162:
//#line 361 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 163:
//#line 362 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");}
break;
case 164:
//#line 363 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 165:
//#line 364 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 166:
//#line 365 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 167:
//#line 366 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 168:
//#line 370 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 169:
//#line 371 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 170:
//#line 372 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 171:
//#line 373 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 172:
//#line 374 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 177:
//#line 390 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 178:
//#line 391 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 179:
//#line 393 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
case 180:
//#line 394 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1286 "Parser.java"
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
