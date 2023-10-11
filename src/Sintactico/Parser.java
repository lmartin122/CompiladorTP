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
    0,    1,    1,    2,    2,    2,    2,    3,    3,    7,
    9,    9,   10,   11,   11,   12,   15,   15,   16,   16,
   17,   18,   13,   20,   22,   23,   23,   23,   23,   23,
   21,   21,   24,   26,    8,   27,   27,    4,   29,   29,
   29,   29,   30,   30,   31,   31,   32,   33,    5,    5,
    5,    5,   35,   36,   36,   37,   38,   39,   40,   41,
   41,   43,   44,   44,   45,   46,   46,   46,   47,   47,
   47,   47,   47,   19,   48,   48,   48,   49,   49,   49,
   49,   49,   50,   50,   51,   51,   51,   52,   52,   52,
   52,   52,   52,   42,   42,   53,   53,   53,   53,   53,
   53,   14,   14,   54,   34,   55,   55,   56,   56,   57,
   28,   25,   25,   25,   25,   59,   59,   58,   58,   60,
   60,    6,    6,   61,   61,   61,   61,   61,   61,   62,
   70,   63,   63,   63,   63,   63,   63,   71,   71,   71,
   68,   72,   72,   69,   64,   64,   64,   64,   64,   64,
   64,   65,   65,   65,   65,   65,   65,   65,   65,   65,
   66,   66,   66,   66,   66,   73,   74,   76,   75,   67,
   67,   67,
};
final static short yylen[] = {                            2,
    1,    1,    2,    1,    1,    1,    1,    3,    4,    3,
    1,    2,    1,    1,    1,    3,    1,    3,    1,    3,
    1,    1,    2,    2,    1,    4,    4,    6,    3,    3,
    1,    1,    2,    1,    2,    1,    3,    3,    3,    3,
    2,    2,    1,    2,    1,    1,    2,    3,    5,    6,
    6,    4,    3,    1,    2,    1,    2,    1,    3,    1,
    1,    3,    1,    1,    1,    1,    3,    3,    1,    3,
    3,    3,    3,    1,    1,    3,    3,    1,    4,    3,
    3,    3,    1,    1,    1,    3,    2,    1,    1,    1,
    2,    2,    2,    1,    1,    4,    3,    6,    4,    3,
    6,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    5,    3,    4,    2,    3,    2,    1,    2,    1,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    2,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    2,    1,    1,    1,    7,    7,    6,    6,    7,    7,
    7,    9,    9,    9,    9,    9,    9,    9,    9,    9,
   12,   12,   13,    8,    8,    1,    1,    1,    1,    3,
    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   25,  109,  108,
  110,    0,    0,  144,    0,    0,    2,    4,    5,    6,
    7,  136,    0,    0,    0,  138,    0,  142,    0,    0,
    0,  143,  102,  104,  106,  107,  122,  123,  133,  134,
  135,  137,  140,  139,    0,  132,    0,    0,  105,    0,
    0,    0,    0,  166,    0,    0,    0,    0,    0,    0,
    0,  115,  118,    0,    3,   21,    0,   17,    0,   32,
   23,   31,    0,   24,   95,   94,    0,    0,    0,  130,
  141,  172,    0,    0,    0,    8,    0,    0,    0,   38,
    0,    0,   84,   88,   89,   90,    0,    0,    0,    0,
    0,    0,    0,   78,   83,   85,    0,    0,    0,  171,
  170,   97,   34,    0,    0,    0,    0,  113,  119,    0,
    0,    0,    0,   59,  100,    0,   62,    0,   52,  111,
    0,   36,    0,   11,   13,   14,   15,    0,  103,    9,
   41,    0,    0,    0,   43,   45,   46,   42,    0,    0,
    0,   91,   93,   92,   87,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   96,  114,    0,   18,   20,   22,   30,    0,
    0,   29,    0,    0,   99,    0,    0,   54,   56,    0,
   10,   12,    0,    0,    0,   39,   44,   40,    0,    0,
    0,    0,  124,  125,  126,  127,  128,  129,    0,   86,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   80,   81,   82,    0,    0,    0,   49,    0,  112,
   33,   27,    0,   26,    0,   58,   57,   53,   55,   37,
   16,   48,  117,    0,  120,    0,    0,    0,    0,   79,
    0,    0,    0,  148,    0,    0,  147,    0,  167,    0,
   51,   50,   98,    0,  101,  116,  121,    0,    0,  150,
    0,    0,  151,  149,    0,    0,  145,    0,    0,  146,
    0,    0,   28,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  164,  165,  169,    0,  157,  158,  160,  159,
  152,  153,  156,  155,  154,    0,  168,    0,    0,    0,
  161,  162,  163,
};
final static short yydgoto[] = {                         15,
   16,   17,   18,   19,   20,   21,   86,   87,  133,  134,
  135,  136,   22,   23,   67,   68,   69,  177,  113,   24,
   71,   25,   74,  181,   26,  114,  131,  132,   90,  144,
  145,  146,  147,  200,  129,  187,  188,  189,  237,   28,
   29,   77,   30,   31,   99,  100,  101,  102,  103,  104,
  105,  106,   32,   33,   34,   35,   36,   64,  201,  244,
  202,   37,   38,  203,  204,  205,  206,  207,  208,   45,
   46,   47,   55,  260,  296,  308,
};
final static short yysindex[] = {                        91,
  -80, -231, -203,   39, -181, -152, -218,    0,    0,    0,
    0,   79,  128,    0,    0,   91,    0,    0,    0,    0,
    0,    0, -155,  -12, -153,    0,    0,    0,  -51,   90,
   95,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  102,    0,  109,  111,    0,  115,
 -110,  -13,  -40,    0, -107, -220,  117,  123,  -34,  -98,
  169,    0,    0,  238,    0,    0,  160,    0,  156,    0,
    0,    0,   -9,    0,    0,    0,  -15,  -25,  -54,    0,
    0,    0,  170,  -50,  171,    0,  181, -101,   51,    0,
  283,  303,    0,    0,    0,    0,  158,  -19,  316,   94,
  -48,    7,  340,    0,    0,    0,  119,  333,  343,    0,
    0,    0,    0,   74,    7,  282,  365,    0,    0, -155,
  -15,  -72,   62,    0,    0,   83,    0,  145,    0,    0,
  364,    0,  -92,    0,    0,    0,    0, -155,    0,    0,
    0, -155, -153,  -85,    0,    0,    0,    0,   57,  122,
  -11,    0,    0,    0,    0,  384,  154,  -15,  -15,  -15,
  -15,  -15,  -15,  -15,  -15,  -11,  -11,  -11,  388,  170,
 -104,  173,    0,    0,  301,    0,    0,    0,    0, -155,
  307,    0,   88,  177,    0,  -12,  -97,    0,    0,  -50,
    0,    0,   -3,  160,  392,    0,    0,    0,  163,    0,
  118,  131,    0,    0,    0,    0,    0,    0,  396,    0,
 -175,  -41,  -35,  -48,  -48,    7,    7,    7,    7,  340,
  340,    0,    0,    0,  -44,  400,  406,    0,  411,    0,
    0,    0,  197,    0,  414,    0,    0,    0,    0,    0,
    0,    0,    0,  174,    0,  122,  415,  122,  416,    0,
  418,  122,  421,    0,  122,  423,    0,  417,    0,  412,
    0,    0,    0,  432,    0,    0,    0,  212,  213,    0,
  215,  216,    0,    0,  217,  218,    0, -192,  219,    0,
  122,   63,    0,  439,  440,  441,  442,  443,  444,  445,
  446,  447,    0,    0,    0,  433,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   63,    0,  452,  272,  -27,
    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   56,    0,    0,    0,  495,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   76,    0,    0,  -38,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  453,    0,   31,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  455,
  -39,  103,    3,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   37,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  377,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   45,    0,    0,    0,    0,    0,   36,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -37,   48,  127,  141,  300,  310,   14,
   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,
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
    0,  485,    0,    0,    0,   41,  424,    0,    0,  371,
    0,    0,  -42,  420, -103,  390,  337,    0,  -26,  -86,
  335,   71,  376,  399,   -6,  448,    0,  334,    0,  434,
  -23,    0,    0,  456,   89,    0,  338,    0,    0,    0,
    0,    0,    0,    0,  429,    0,  195,  386,  252,   43,
    0, -188,    0,    0,    0,    0,    0,    0, -112,    0,
  222,    0,    0,   67,   93,  100,  107,  132,  138,    0,
    0,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=605;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         98,
   97,   66,  254,   67,   97,   98,  112,   64,  257,   76,
   97,  162,   85,  163,   98,  125,  313,   72,  128,   97,
   98,  155,   61,  141,   98,   97,   89,  238,   98,   97,
  123,   70,  191,   97,  193,  108,  259,   57,  194,  196,
  241,  186,  137,   75,  212,   75,   75,   75,   51,  164,
  124,  165,  179,   63,   76,  120,   76,   76,   76,   49,
   58,   75,   75,  290,   75,   77,   39,   77,   77,   77,
  291,   19,   76,   76,   19,   76,   52,   74,   53,   39,
   74,   63,   39,   77,   77,   47,   77,  251,   68,   19,
  137,  148,   40,  295,  178,   74,   60,  198,   49,   41,
  186,  105,  182,   48,  119,   40,   42,   97,   40,   88,
   13,   56,   41,  122,  173,   41,  105,  307,   59,   42,
  197,   63,   42,  185,   66,  197,   73,   75,  234,   78,
   39,   43,  172,  268,   14,  271,   60,   44,   76,  275,
   79,  184,  278,   69,   43,   80,  233,   43,   84,   77,
   44,  227,   81,   44,   82,   19,   40,  107,  143,  143,
  110,   74,   69,   41,   69,   14,  111,   72,  293,   47,
   42,   14,   83,    8,    9,   10,   11,    8,   49,   72,
   48,   73,    8,    9,   10,   11,   72,   49,   72,    8,
    9,   10,   11,  209,   49,   43,  311,   14,   48,   49,
   73,   44,   73,    9,   10,   11,   14,   49,  222,  223,
  224,  258,  116,   13,  143,   91,  121,   14,  120,  143,
  252,  253,   75,  160,  161,  127,  255,  256,   92,  130,
   66,   66,   67,   67,   92,   61,   94,   95,   96,   93,
   94,   95,   96,   92,  199,   93,   94,   95,   96,   92,
   13,   48,   62,   92,   93,   94,   95,   96,  226,  228,
   93,   94,   95,   96,   93,   94,   95,   96,   93,   94,
   95,   96,   75,   75,   75,   75,  199,   75,   75,   75,
   75,   14,   75,   76,   76,   76,   76,  243,   76,   76,
   76,   76,  128,   76,   77,   77,   77,   77,  266,   77,
   77,   77,   77,   85,   77,   19,   19,   19,   19,   60,
   19,   74,   74,   74,   74,   14,   74,   68,   68,   47,
   47,   47,   47,  150,   47,    8,    9,   10,   11,  105,
   49,    8,    9,   10,   11,  105,   49,    9,   10,   11,
   70,   49,  151,   94,   95,   96,    1,    2,    3,   60,
   71,    4,  214,  215,    5,  103,  157,    6,    7,   70,
   13,   70,  118,  158,  159,    8,    9,   10,   11,   71,
   12,   71,   69,   69,   69,   69,  168,   60,  213,  246,
  247,  166,    4,   60,  169,    5,  167,   61,    4,    7,
  170,    5,  248,  249,  199,    7,   72,   72,   72,   72,
  171,   12,    8,    9,   10,   11,  174,   12,  175,  211,
   73,   73,   73,   73,    4,  220,  221,    5,   60,    8,
  245,    7,  190,    4,  210,  230,    5,  225,  229,   60,
    7,  232,  235,   12,    4,  242,  250,    5,  152,  153,
  154,    7,   12,  261,  115,    8,    9,   10,   11,  262,
   49,  263,  264,   12,  265,   27,   50,  281,  270,  273,
   54,  274,  115,  115,  277,  267,  280,  269,   27,  272,
  282,   27,  283,  276,  284,  285,  279,  286,  287,  288,
  289,  292,  297,  298,  299,  300,  301,  302,  303,  304,
  305,  306,  309,   60,    1,   65,  131,  117,    4,   35,
   65,    5,  294,  192,  138,    7,  115,  142,  142,  176,
  140,  109,    8,    9,   10,   11,  231,   12,  195,   27,
  236,  183,  149,  240,  239,  126,  156,  310,    0,    0,
  312,    0,    4,    0,    0,    5,    0,    0,    0,    7,
  139,  180,  180,  139,  139,  216,  217,  218,  219,    0,
    0,   12,  138,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  142,    0,    0,    0,    0,  142,   70,
   70,   70,   70,    0,    0,    0,    0,  139,  139,   71,
   71,   71,   71,    0,    0,    0,    0,    0,  139,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  139,
    0,    0,    0,    0,  139,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   45,   41,   44,   41,   45,   40,   41,   46,   44,   61,
   45,   60,  123,   62,   40,   41,   44,   24,  123,   45,
   40,   41,   61,  125,   40,   45,   40,  125,   40,   45,
   40,   44,  125,   45,  138,  256,  225,  256,  142,  125,
   44,  128,   85,   41,  157,   43,   44,   45,  280,   43,
   77,   45,  125,   13,   41,   59,   43,   44,   45,  280,
  279,   59,   60,  256,   62,   41,    0,   43,   44,   45,
  263,   41,   59,   60,   44,   62,  280,   41,   40,   13,
   44,   46,   16,   59,   60,   41,   62,  263,   41,   59,
  133,   41,    0,  282,  121,   59,   61,   41,  280,    0,
  187,   46,   41,  279,   64,   13,    0,   45,   16,  123,
  123,  264,   13,  123,   41,   16,   61,  306,   40,   13,
  144,   46,   16,   41,  280,  149,  280,  125,   41,   40,
   64,    0,   59,  246,   44,  248,   61,    0,  125,  252,
   46,   59,  255,   41,   13,   44,   59,   16,  259,  125,
   13,  256,   44,   16,   44,  125,   64,  265,   88,   89,
   44,  125,   60,   64,   62,   44,   44,   41,  281,  125,
   64,   44,   58,  275,  276,  277,  278,  275,  280,  186,
  279,   41,  275,  276,  277,  278,   60,  280,   62,  275,
  276,  277,  278,  151,  280,   64,  309,   44,  279,  280,
   60,   64,   62,  276,  277,  278,   44,  280,  166,  167,
  168,  256,   44,  123,  144,  256,   61,   44,   59,  149,
  262,  263,  274,  272,  273,  280,  262,  263,  269,  280,
  270,  271,  270,  271,  269,  274,  281,  282,  283,  280,
  281,  282,  283,  269,  123,  280,  281,  282,  283,  269,
  123,  279,  125,  269,  280,  281,  282,  283,  170,  171,
  280,  281,  282,  283,  280,  281,  282,  283,  280,  281,
  282,  283,  270,  271,  272,  273,  123,  275,  276,  277,
  278,   44,  280,  270,  271,  272,  273,  125,  275,  276,
  277,  278,  123,  280,  270,  271,  272,  273,  125,  275,
  276,  277,  278,  123,  280,  275,  276,  277,  278,  274,
  280,  275,  276,  277,  278,   44,  280,  270,  271,  275,
  276,  277,  278,   41,  280,  275,  276,  277,  278,  274,
  280,  275,  276,  277,  278,  280,  280,  276,  277,  278,
   41,  280,   40,  281,  282,  283,  256,  257,  258,  274,
   41,  261,  158,  159,  264,  280,   41,  267,  268,   60,
  123,   62,  125,  270,  271,  275,  276,  277,  278,   60,
  280,   62,  270,  271,  272,  273,   37,  256,  157,  262,
  263,   42,  261,  256,  266,  264,   47,  260,  261,  268,
   58,  264,  262,  263,  123,  268,  270,  271,  272,  273,
   58,  280,  275,  276,  277,  278,  125,  280,   44,  256,
  270,  271,  272,  273,  261,  164,  165,  264,  256,  275,
  199,  268,   59,  261,   41,  125,  264,   40,  256,  256,
  268,  125,  256,  280,  261,   44,   41,  264,  281,  282,
  283,  268,  280,   44,   59,  275,  276,  277,  278,   44,
  280,   41,  256,  280,   41,    0,    1,   41,   44,   44,
    5,   44,   77,   78,   44,  244,   44,  246,   13,  248,
   59,   16,   41,  252,  263,  263,  255,  263,  263,  263,
  263,  263,   44,   44,   44,   44,   44,   44,   44,   44,
   44,   59,   41,  256,    0,   41,   44,  260,  261,  123,
   16,  264,  281,  133,   85,  268,  121,   88,   89,  120,
   87,   56,  275,  276,  277,  278,  180,  280,  143,   64,
  186,  123,   89,  190,  187,   78,   98,  256,   -1,   -1,
  309,   -1,  261,   -1,   -1,  264,   -1,   -1,   -1,  268,
   85,  122,  123,   88,   89,  160,  161,  162,  163,   -1,
   -1,  280,  133,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  144,   -1,   -1,   -1,   -1,  149,  270,
  271,  272,  273,   -1,   -1,   -1,   -1,  122,  123,  270,
  271,  272,  273,   -1,   -1,   -1,   -1,   -1,  133,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  144,
   -1,   -1,   -1,   -1,  149,
};
}
final static short YYFINAL=15;
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
"implement_for_declaration : error reference_type ':' implement_for_body",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
"implement_for_method_body : method_body",
"assignment : left_hand_side assignment_operator arithmetic_operation",
"left_hand_side : reference_type",
"left_hand_side : field_acces",
"field_acces : primary '.' ID",
"primary : reference_type",
"primary : field_acces",
"expression : equality_expression",
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
"multiplicative_expression : TOD '(' unary_expression ')'",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : term",
"unary_expression : ID",
"term : factor",
"term : '(' expression ')'",
"term : '(' ')'",
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
"block : '{' RETURN ',' '}'",
"block : '{' '}'",
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
"statement_expression : assignment",
"statement_expression : method_invocation",
"empty_statement : ','",
"if_then_statement : IF '(' expression ')' executable_block END_IF ','",
"if_then_statement : IF '(' expression ')' executable_statament END_IF ','",
"if_then_statement : IF '(' expression ')' executable_statament ','",
"if_then_statement : IF '(' expression ')' executable_block ','",
"if_then_statement : IF '(' expression ')' error END_IF ','",
"if_then_statement : IF '(' error ')' executable_block END_IF ','",
"if_then_statement : IF '(' error ')' executable_statament END_IF ','",
"if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_statament END_IF ','",
"if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block error ','",
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
"print_statement : PRINT error ','",
"print_statement : error CADENA ','",
};

//#line 388 "grammer.y"

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
    aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
    System.out.println(aLexico.getProgram());
}


//#line 840 "Parser.java"
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
//#line 31 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa");}
break;
case 8:
//#line 49 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
break;
case 9:
//#line 50 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
break;
case 16:
//#line 67 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
break;
case 26:
//#line 93 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 27:
//#line 94 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
break;
case 28:
//#line 95 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 29:
//#line 96 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 30:
//#line 97 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
break;
case 38:
//#line 119 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 40:
//#line 123 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
break;
case 42:
//#line 125 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
break;
case 49:
//#line 142 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 50:
//#line 143 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 51:
//#line 144 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 52:
//#line 145 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");}
break;
case 59:
//#line 169 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
break;
case 65:
//#line 183 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 74:
//#line 198 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 87:
//#line 219 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 90:
//#line 224 "grammer.y"
{yyval = new ParserVal(ChequeoRangoEntero(val_peek(0).sval));}
break;
case 91:
//#line 225 "grammer.y"
{System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval); yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 92:
//#line 226 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 93:
//#line 227 "grammer.y"
{Logger.logWarning(aLexico.getProgramPosition() ,"Los tipos enteros deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 96:
//#line 234 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 97:
//#line 235 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 98:
//#line 236 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 99:
//#line 237 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 100:
//#line 238 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 101:
//#line 239 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 113:
//#line 281 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 115:
//#line 283 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 130:
//#line 311 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 145:
//#line 342 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 146:
//#line 343 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 147:
//#line 344 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 148:
//#line 345 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 149:
//#line 346 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
break;
case 150:
//#line 347 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 151:
//#line 348 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 152:
//#line 352 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 153:
//#line 353 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 154:
//#line 354 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 155:
//#line 355 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 156:
//#line 356 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");}
break;
case 157:
//#line 357 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 158:
//#line 358 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 159:
//#line 359 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 160:
//#line 360 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 161:
//#line 364 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 162:
//#line 365 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 163:
//#line 366 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 164:
//#line 367 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 165:
//#line 368 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 170:
//#line 383 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 171:
//#line 384 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 172:
//#line 385 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
//#line 1217 "Parser.java"
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
