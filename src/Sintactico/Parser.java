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
    0,    1,    1,    2,    2,    2,    2,    3,    3,    3,
    7,    9,    9,   10,   11,   11,   12,   15,   15,   16,
   16,   17,   18,   13,   20,   22,   23,   23,   23,   23,
   23,   21,   21,   24,   26,    8,   27,   27,    4,    4,
   29,   29,   29,   29,   30,   30,   31,   31,    5,    5,
    5,    5,   33,   34,   34,   35,   36,   37,   38,   39,
   41,   42,   42,   42,   43,   43,   43,   43,   43,   19,
   44,   44,   44,   45,   45,   45,   45,   45,   46,   46,
   47,   47,   47,   48,   48,   48,   48,   48,   48,   40,
   40,   49,   49,   49,   14,   14,   50,   32,   51,   51,
   52,   52,   53,   28,   25,   25,   25,   25,   55,   55,
   54,   54,   56,   56,    6,    6,   57,   57,   57,   57,
   57,   57,   58,   66,   59,   59,   59,   59,   59,   59,
   67,   67,   67,   64,   68,   68,   65,   60,   60,   60,
   60,   60,   60,   60,   61,   61,   61,   61,   61,   61,
   61,   61,   61,   62,   62,   62,   62,   62,   69,   70,
   72,   71,   63,   63,   63,
};
final static short yylen[] = {                            2,
    1,    1,    2,    1,    1,    1,    1,    3,    4,    3,
    3,    1,    2,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    2,    1,    4,    4,    6,    3,
    3,    1,    1,    2,    1,    2,    1,    3,    3,    4,
    3,    3,    2,    2,    1,    2,    2,    3,    5,    6,
    6,    4,    3,    1,    2,    1,    2,    1,    3,    1,
    1,    1,    3,    3,    1,    3,    3,    3,    3,    1,
    1,    3,    3,    1,    4,    3,    3,    3,    1,    1,
    1,    3,    2,    1,    1,    1,    2,    2,    2,    1,
    1,    5,    4,    7,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    5,    3,    4,    2,    3,    2,
    1,    2,    1,    2,    1,    1,    1,    1,    1,    1,
    1,    1,    2,    2,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    7,    7,    6,
    6,    7,    7,    7,    9,    9,    9,    9,    9,    9,
    9,    9,    9,   12,   12,   13,    8,    8,    1,    1,
    1,    1,    3,    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   26,  102,  101,
  103,    0,    0,  137,    0,    0,    2,    4,    5,    6,
    7,  129,    0,    0,    0,  131,    0,  135,    0,  136,
   95,   97,   99,  100,  115,  116,  126,  127,  128,  130,
  133,  132,    0,  125,  134,    0,    0,    0,    0,    0,
    0,   98,  159,    0,    0,    0,    0,    0,    0,    0,
  108,  111,    0,    3,   22,    0,   18,    0,   33,   24,
   32,    0,   25,   91,   90,    0,  123,  165,    0,   10,
    0,    0,    8,    0,    0,    0,    0,   39,    0,    0,
   80,   84,   85,   86,    0,    0,    0,    0,    0,    0,
    0,   74,   79,   81,    0,    0,    0,  164,  163,    0,
   35,    0,    0,    0,    0,  106,  112,    0,    0,    0,
    0,   59,    0,   12,   14,   15,   16,    0,   96,    0,
   52,  104,    0,   37,    9,   40,   43,    0,    0,   45,
   44,    0,    0,    0,   87,   89,   88,   83,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   93,    0,    0,  107,    0,   19,
   21,   23,   31,    0,    0,   30,    0,   11,   13,    0,
    0,    0,   54,   56,    0,    0,   47,   41,   46,   42,
    0,   60,    0,    0,  117,  118,  119,  120,  121,  122,
    0,   82,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   76,   77,   78,    0,    0,    0,   49,
    0,   92,  105,   34,   28,    0,   27,   17,   58,   57,
   53,   55,   38,   48,  110,    0,  113,    0,    0,    0,
    0,   75,    0,    0,    0,  141,    0,    0,  140,    0,
  160,    0,   51,   50,    0,    0,  109,  114,    0,    0,
  143,    0,    0,  144,  142,    0,    0,  138,    0,    0,
  139,    0,    0,   94,   29,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  157,  158,  162,    0,  150,  151,
  153,  152,  145,  146,  149,  148,  147,    0,  161,    0,
    0,    0,  154,  155,  156,
};
final static short yydgoto[] = {                         15,
   16,   17,   18,   19,   20,   21,   80,   84,  123,  124,
  125,  126,   22,   23,   66,   67,   68,  171,  111,   24,
   70,   25,   73,  175,   26,  112,  133,  134,   88,  139,
  140,  192,  131,  182,  183,  184,  230,   28,   29,   76,
   97,   98,   99,  100,  101,  102,  103,  104,   30,   31,
   32,   33,   34,   63,  193,  236,  194,   35,   36,  195,
  196,  197,  198,  199,  200,   43,   44,   45,   54,  252,
  288,  300,
};
final static short yysindex[] = {                       121,
 -214, -191, -173,   69, -164, -139, -215,    0,    0,    0,
    0,   94,  261,    0,    0,  121,    0,    0,    0,    0,
    0,    0, -137,  -20, -133,    0,    0,    0,   -4,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  108,    0,    0,  113,   43,  111, -104,  -24,
  -40,    0,    0,  -88, -221,  135,  137,  -34,  -90,  139,
    0,    0,  286,    0,    0,  145,    0,  141,    0,    0,
    0,  -18,    0,    0,    0,  -30,    0,    0,  195,    0,
   83,  -71,    0,   43,  171, -117,  -38,    0,  179,  196,
    0,    0,    0,    0,  -96,   78,  204, -141,  134,   96,
   91,    0,    0,    0,    9,  227,  237,    0,    0,  236,
    0,  -12,   96,  185,  276,    0,    0, -137,  -30, -105,
   90,    0,  -85,    0,    0,    0,    0, -137,    0,   50,
    0,    0,  298,    0,    0,    0,    0, -218, -111,    0,
    0,   -9,  -42,   82,    0,    0,    0,    0,  294,  144,
  -30,  -30,  -30,  -30,  -30,  -30,  -30,  -30,   82,   82,
   82,  336,   83, -102,    0,  124,  350,    0,  270,    0,
    0,    0,    0, -137,  285,    0,   -5,    0,    0,   -1,
  -20, -108,    0,    0,  -71,  373,    0,    0,    0,    0,
  166,    0, -103, -100,    0,    0,    0,    0,    0,    0,
  378,    0, -165,  -35,  -32,  134,  134,   96,   96,   96,
   96,   91,   91,    0,    0,    0,   92,  376,  381,    0,
  385,    0,    0,    0,    0,  173,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  167,    0,  -42,  388,  -42,
  392,    0,  399,  -42,  400,    0,  -42,  404,    0,  408,
    0,  391,    0,    0,  407,  413,    0,    0,  200,  213,
    0,  215,  216,    0,    0,  217,  218,    0, -148,  219,
    0,  -42,  -27,    0,    0,  415,  423,  439,  441,  443,
  444,  445,  446,  447,    0,    0,    0,  425,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -27,    0,  451,
  177,  -19,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   75,    0,    0,    0,  486,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  107,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  435,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  453,    0,   62,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  454,  -37,  120,
    1,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   76,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  379,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   46,   71,  143,  309,  342,
  377,   26,   51,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  480,    0,    0,    0,   21,  -16,    0,    0,  383,
    0,    0,  -49,  -48,  380,  386,  333,    0,  -28,  -81,
  328,  -59,  372,  390,  -11,    0,    0,  327,    0,  426,
    6,   77,   49,    0,  332,    0,    0,    0,    0,    0,
  420,    0,   66,  345,  188,   64,    0, -194,    0,    0,
    0,    0,    0,    0,  222,    0,  -94,    0,    0,   37,
   39,  440,  442,  452,  461,    0,    0,    0,    0,    0,
    0,    0,
};
final static int YYTABLESIZE=650;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         96,
   71,   14,  141,   62,   95,   96,  110,  137,  246,   96,
   95,  249,   71,  188,   95,   87,  231,   95,   79,  173,
  130,  121,  251,   69,  305,   72,  138,  138,  167,  127,
  128,  190,   83,   62,  106,  227,   37,  186,   38,  178,
   56,   71,  228,   71,   71,   71,  166,  122,  181,   37,
   73,   38,   37,  226,   38,  205,   75,  118,   52,   71,
   71,   72,   71,   57,   46,   47,   72,  135,   72,   72,
   72,  174,  174,  127,  128,   70,   27,   48,  287,  138,
  191,   53,  138,  117,   72,   72,   63,   72,   49,   27,
  172,   73,   27,   73,   73,   73,  237,  243,   86,   37,
  181,   38,   13,  299,  120,   20,   50,  282,   51,   73,
   73,   64,   73,   46,  283,   52,   70,   96,  148,   70,
   20,   96,   95,   71,   55,   71,   95,  161,  151,  152,
  176,  107,  159,   58,   70,   98,   95,  160,  157,   27,
  158,  258,   65,  260,  189,  263,   72,  189,   72,  267,
   72,   77,  270,  219,   82,  129,   78,    8,  238,  239,
   65,  240,  241,    8,   14,   79,    8,   60,   81,   71,
    9,   10,   11,   73,   52,   73,  105,  286,  108,   65,
  109,   65,  114,   68,  145,  146,  147,   14,   46,    8,
    9,   10,   11,  155,   52,  156,  129,  129,   70,  129,
   70,  119,   68,  118,   68,  130,  304,  201,  132,   14,
   14,  218,  220,   59,  136,   89,  206,  207,    4,  143,
   14,    5,  214,  215,  216,    7,  244,  245,   90,  247,
  248,   85,   62,   62,   90,  144,    8,   12,   90,   91,
   92,   93,   94,   13,  150,   91,   92,   93,   94,   91,
   92,   93,   94,   92,   93,   94,   71,   71,   71,   46,
   71,   71,   71,   71,   71,    8,  191,   71,   71,   74,
   71,   71,   71,   71,  162,   71,   71,   71,   71,  165,
   71,   72,   72,   72,  163,   72,   72,   72,   72,   72,
  235,  257,   72,   72,  164,   72,   72,   72,   72,  191,
   72,   72,   72,   72,   14,   72,   73,   73,   73,  168,
   73,   73,   73,   73,   73,   63,   63,   73,   73,  169,
   73,   73,   73,   73,    8,   73,   73,   73,   73,   14,
   73,   70,   70,   70,  202,   70,   70,   70,   70,   70,
   64,   64,   70,   70,  212,  213,   90,  250,   98,   69,
   70,   70,   70,   70,   98,   70,  185,   91,   92,   93,
   94,   91,   92,   93,   94,    9,   10,   11,   69,   52,
   69,  204,   92,   93,   94,  217,    1,    2,    3,  221,
   60,    4,   66,   13,    5,   61,   96,    6,    7,   65,
   65,   65,   65,  222,  223,    8,    9,   10,   11,  203,
   12,   66,  113,   66,    4,  153,  154,    5,   13,  225,
  116,    7,   68,   68,   68,   68,  234,   67,  242,  253,
  113,   59,   59,   12,  254,  255,    4,    4,  256,    5,
    5,  261,  302,    7,    7,  264,   67,    4,   67,   39,
    5,   40,  265,  268,    7,   12,   12,  271,  272,  273,
  274,   41,   39,  275,   40,   39,   12,   40,  289,  259,
   42,  262,  276,  113,   41,  266,  290,   41,  269,    8,
    9,   10,   11,   42,   52,  277,   42,  278,  279,  280,
  281,  284,  291,  298,  292,    1,  293,  294,  295,  296,
  297,  301,   98,  285,   61,   64,  124,  208,  209,  210,
  211,   36,   39,  170,   40,  179,  224,  180,  229,  187,
  177,  233,  142,  232,   41,  149,   59,    0,    0,    0,
   60,    4,  303,   42,    5,    0,    0,    0,    7,    0,
    0,    0,    0,    0,    0,    8,    9,   10,   11,    0,
   12,   59,    0,    0,    0,  115,    4,    0,    0,    5,
    0,    0,    0,    7,    0,    0,    0,    0,    0,    0,
    8,    9,   10,   11,    0,   12,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   69,   69,
   69,   69,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   66,   66,   66,   66,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   67,   67,   67,   67,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   44,   41,   41,   45,   40,   41,  125,   44,   40,
   45,   44,   24,  125,   45,   40,  125,   45,  123,  125,
  123,   40,  217,   44,   44,    0,   86,   87,   41,   79,
   79,   41,   49,   13,  256,   41,    0,  256,    0,  125,
  256,   41,   44,   43,   44,   45,   59,   76,  130,   13,
    0,   13,   16,   59,   16,  150,   61,   59,  280,   59,
   60,  280,   62,  279,  279,  280,   41,   84,   43,   44,
   45,  120,  121,  123,  123,    0,    0,    1,  273,  139,
  123,    5,  142,   63,   59,   60,   41,   62,  280,   13,
  119,   41,   16,   43,   44,   45,  191,  263,  123,   63,
  182,   63,  123,  298,  123,   44,  280,  256,   40,   59,
   60,   41,   62,  279,  263,  280,   41,   40,   41,   44,
   59,   40,   45,  123,  264,  125,   45,   37,  270,  271,
   41,   55,   42,   40,   59,   61,   45,   47,   43,   63,
   45,  236,  280,  238,  139,  240,  280,  142,  123,  244,
  125,   44,  247,  256,  259,   79,   44,  275,  262,  263,
   41,  262,  263,  275,   44,  123,  275,   61,   58,  181,
  276,  277,  278,  123,  280,  125,  265,  272,   44,   60,
   44,   62,   44,   41,  281,  282,  283,   44,  279,  275,
  276,  277,  278,   60,  280,   62,  120,  121,  123,  123,
  125,   61,   60,   59,   62,  123,  301,  144,  280,   44,
   44,  163,  164,  256,   44,  256,  151,  152,  261,   41,
   44,  264,  159,  160,  161,  268,  262,  263,  269,  262,
  263,  256,  270,  271,  269,   40,  275,  280,  269,  280,
  281,  282,  283,  123,   41,  280,  281,  282,  283,  280,
  281,  282,  283,  281,  282,  283,  256,  257,  258,  279,
  260,  261,  262,  263,  264,  275,  123,  267,  268,  274,
  270,  271,  272,  273,  266,  275,  276,  277,  278,   44,
  280,  256,  257,  258,   58,  260,  261,  262,  263,  264,
  125,  125,  267,  268,   58,  270,  271,  272,  273,  123,
  275,  276,  277,  278,   44,  280,  256,  257,  258,  125,
  260,  261,  262,  263,  264,  270,  271,  267,  268,   44,
  270,  271,  272,  273,  275,  275,  276,  277,  278,   44,
  280,  256,  257,  258,   41,  260,  261,  262,  263,  264,
  270,  271,  267,  268,  157,  158,  269,  256,  274,   41,
  275,  276,  277,  278,  280,  280,   59,  280,  281,  282,
  283,  280,  281,  282,  283,  276,  277,  278,   60,  280,
   62,  150,  281,  282,  283,   40,  256,  257,  258,  256,
  274,  261,   41,  123,  264,  125,  280,  267,  268,  270,
  271,  272,  273,   44,  125,  275,  276,  277,  278,  256,
  280,   60,   58,   62,  261,  272,  273,  264,  123,  125,
  125,  268,  270,  271,  272,  273,   44,   41,   41,   44,
   76,  256,  256,  280,   44,   41,  261,  261,  256,  264,
  264,   44,  256,  268,  268,   44,   60,  261,   62,    0,
  264,    0,   44,   44,  268,  280,  280,   44,   41,   59,
   44,    0,   13,   41,   13,   16,  280,   16,   44,  238,
    0,  240,  263,  119,   13,  244,   44,   16,  247,  275,
  276,  277,  278,   13,  280,  263,   16,  263,  263,  263,
  263,  263,   44,   59,   44,    0,   44,   44,   44,   44,
   44,   41,   58,  272,   41,   16,   44,  153,  154,  155,
  156,  123,   63,  118,   63,  123,  174,  128,  181,  138,
  121,  185,   87,  182,   63,   96,  256,   -1,   -1,   -1,
  260,  261,  301,   63,  264,   -1,   -1,   -1,  268,   -1,
   -1,   -1,   -1,   -1,   -1,  275,  276,  277,  278,   -1,
  280,  256,   -1,   -1,   -1,  260,  261,   -1,   -1,  264,
   -1,   -1,   -1,  268,   -1,   -1,   -1,   -1,   -1,   -1,
  275,  276,  277,  278,   -1,  280,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  270,  271,
  272,  273,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  270,  271,  272,  273,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  270,  271,  272,  273,
};
}
final static short YYFINAL=15;
final static short YYMAXTOKEN=283;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,"':'",
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
"class_declaration : error ID class_body",
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
"interface_declaration : INTERFACE ID error ','",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '(' interface_member_declaration ')'",
"interface_body : '{' '}'",
"interface_body : '(' ')'",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : result_type method_declarator",
"interface_method_declaration : result_type error ','",
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
"method_invocation : ID '(' real_parameter ')' ','",
"method_invocation : ID '(' ')' ','",
"method_invocation : ID '(' real_parameter ';' error ')' ','",
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
"expression_statement : statement_expression",
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

//#line 374 "grammer.y"

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
      indice = Integer.parseInt(input);
      if (indice < directories.size() || indice >= 0) {
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
        indice = Integer.parseInt(input);

        if (indice < directories.size() || indice >= 0) {
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

    System.out.println(aLexico.getProgram());

    Parser aSintactico = new Parser();
    aSintactico.run();
    aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
}


//#line 826 "Parser.java"
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
//#line 50 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
break;
case 9:
//#line 51 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
break;
case 10:
//#line 52 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de CLASS no valida.");}
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
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
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
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
break;
case 39:
//#line 121 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 40:
//#line 122 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la interface.");}
break;
case 42:
//#line 126 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
break;
case 44:
//#line 128 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
break;
case 48:
//#line 136 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario generar la declaracion del metodo");}
break;
case 49:
//#line 139 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 50:
//#line 140 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 51:
//#line 141 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 52:
//#line 142 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");}
break;
case 59:
//#line 166 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
break;
case 61:
//#line 172 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 70:
//#line 187 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 83:
//#line 208 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 86:
//#line 213 "grammer.y"
{yyval = new ParserVal(ChequeoRangoEntero(val_peek(0).sval));}
break;
case 87:
//#line 214 "grammer.y"
{System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval); yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 88:
//#line 215 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 89:
//#line 216 "grammer.y"
{Logger.logWarning(aLexico.getProgramPosition() ,"Los tipos enteros deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 92:
//#line 223 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");}
break;
case 93:
//#line 224 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");}
break;
case 94:
//#line 225 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 106:
//#line 267 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 108:
//#line 269 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 123:
//#line 297 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 138:
//#line 328 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 139:
//#line 329 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 140:
//#line 330 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 141:
//#line 331 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 142:
//#line 332 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
break;
case 143:
//#line 333 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 144:
//#line 334 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 145:
//#line 338 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 146:
//#line 339 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 147:
//#line 340 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 148:
//#line 341 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 149:
//#line 342 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");}
break;
case 150:
//#line 343 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 151:
//#line 344 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 152:
//#line 345 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 153:
//#line 346 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 154:
//#line 350 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 155:
//#line 351 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 156:
//#line 352 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 157:
//#line 353 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 158:
//#line 354 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 163:
//#line 369 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 164:
//#line 370 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 165:
//#line 371 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
//#line 1203 "Parser.java"
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
