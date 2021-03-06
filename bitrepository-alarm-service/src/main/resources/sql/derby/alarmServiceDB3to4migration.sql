---
-- #%L
-- Bitrepository Alarm Service
-- %%
-- Copyright (C) 2010 - 2013 The State and University Library, The Royal Library and The State Archives, Denmark
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Lesser General Public License as 
-- published by the Free Software Foundation, either version 2.1 of the 
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Lesser Public License for more details.
-- 
-- You should have received a copy of the GNU General Lesser Public 
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/lgpl-2.1.html>.
-- #L%
---
-- Integrity DB migration from version 2 to 3

connect 'jdbc:derby:alarmservicedb';

-- Update table versions.
UPDATE tableversions SET version=4 WHERE tablename='alarm';
UPDATE tableversions SET version=4 WHERE tablename='alarmservicedb';

ALTER TABLE alarm ADD COLUMN alarm_date2 BIGINT;

DROP INDEX alarmdateindex;

UPDATE alarm SET alarm_date2 = ({fn timestampdiff(SQL_TSI_FRAC_SECOND, timestamp('1970-1-1-00.00.00.000000'), alarm_date)} / 1000000);

ALTER TABLE alarm DROP COLUMN alarm_date;

RENAME COLUMN alarm.alarm_date2 TO alarm_date;

ALTER TABLE alarm ALTER COLUMN alarm_date NOT NULL;

CREATE INDEX alarmdateindex ON alarm (alarm_date);

