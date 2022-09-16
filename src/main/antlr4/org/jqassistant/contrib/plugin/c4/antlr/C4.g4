grammar C4;

c4:
    Startuml (name=Param)? NL+
    (Preproc NL+)*
    element*
    (ShowLegend NL*)?
    Enduml NL*
    EOF;

element: ((addProperty NL+)* ((c4Element hierarchy?) | rel | biRel) NL+);
addProperty: AddProperty LB key=Param CM value=Param RB;

c4Element: component | container | system | person | boundary;
hierarchy: (RCB NL* element* LCB);
component returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (Component { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | ComponentExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | ComponentDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | ComponentDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | ComponentQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | ComponentQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    paramList;

container returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (Container { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | ContainerExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | ContainerDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | ContainerDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | ContainerQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | ContainerQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    paramList;

system returns [org.jqassistant.contrib.plugin.c4.data.SecondaryElementType secondaryType, boolean external]
    : (System { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; }
    | SystemExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DEFAULT; $external = true; }
    | SystemDb { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; }
    | SystemDbExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.DB; $external = true; }
    | SystemQueue { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; }
    | SystemQueueExt { $secondaryType = org.jqassistant.contrib.plugin.c4.data.SecondaryElementType.QUEUE; $external = true; })
    systemParamList;

person returns [boolean external]
    : (Person
    | PersonExt { $external = true; })
    systemParamList;

boundary returns [String type]
    : Boundary genericBoundaryParamList
    | EnterpriseBoundary boundaryParamList { $type = "Enterprise"; }
    | SystemBoundary boundaryParamList { $type = "System"; }
    | Container boundaryParamList { $type = "Container"; };

paramList: LB p1=Param CM p2=Param CM p3=Param (CM p4=Param (CM p5=Param (CM p6=Param (CM p7=Param)?)?)?)? RB;
systemParamList: LB p1=Param CM p2=Param (CM p3=Param (CM p4=Param (CM p5=Param (CM p6=Param)?)?)?)? RB;
genericBoundaryParamList: LB p1=Param CM p2=Param (CM p3=Param (CM p4=Param (CM p5=Param)?)?)? RB;
boundaryParamList: LB p1=Param CM p2=Param (CM p3=Param (CM p4=Param)?)? RB;
rel: Rel relParamList;
biRel: BiRel relParamList;
relParamList: LB p1=Param CM p2=Param CM p3=Param (CM p4=Param (CM p5=Param (CM p6=Param(CM p7=Param (CM p8=Param)?)?)?)?)? RB;

Startuml: '@startuml';
Enduml: '@enduml';
AddProperty: 'AddProperty';
Component: 'Component';
ComponentExt: Component Ext;
ComponentDb: Component DB;
ComponentDbExt: ComponentDb Ext;
ComponentQueue: Component Queue;
ComponentQueueExt: ComponentQueue Ext;
Container: 'Container';
ContainerExt: Container Ext;
ContainerDb: Container DB;
ContainerDbExt: ContainerDb Ext;
ContainerQueue: Container Queue;
ContainerQueueExt: ContainerQueue Ext;
CountainerBoundary: Container US Boundary;
System: 'System';
SystemExt: System Ext;
SystemDb: System DB;
SystemDbExt: SystemDb Ext;
SystemQueue: System Queue;
SystemQueueExt: System Queue Ext;
SystemBoundary: System US Boundary;
Enterprise: 'Enterprise';
EnterpriseBoundary: Enterprise US Boundary;
Person: 'Person';
PersonExt: Person Ext;
Boundary: 'Boundary';
DB: 'Db';
Queue: 'Queue';
Ext: '_Ext';
Rel: 'Rel'| 'Rel_U' | 'Rel_Up' | 'Rel_D' | 'Rel_Down' | 'Rel_L' | 'Rel_Left' | 'Rel_R' | 'Rel_Right';
BiRel: 'Bi' Rel;
Param: STRING | KeyValue;
KeyValue: DL CHAR+ WS* '=' WS* STRING;
Preproc: '!' ~[\r\n]*;
ShowLegend: 'SHOW_LEGEND' ~[\r\n]*;

STRING: '"' ~["\r\n]* '"' | CHAR+;
CHAR: ~[",)({}@!\r\n ];
//STRING: ('A'..'Z' | 'a'..'z' | [-_+/] | [äöüÄÖÜß] | INT)+;
INT: ('0'..'9')+;
CM: ',';
RCB: '{';
LCB: '}';
LB: '(';
RB: ')';
US: '_';
DL: '$';
WS: (' ' | '\t') -> skip;
NL:  '\r'? '\n';
