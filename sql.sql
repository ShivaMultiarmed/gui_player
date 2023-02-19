SELECT `tracks`.`id`, `tracks`.`file_id`, `tracks`.`title`, `tracks`.`artist`, `playlists`.`userid`
FROM `tracks`
                    INNER JOIN `tracks_in_playlists`
                    ON `tracks_in_playlists`.`trackid` = `tracks`.`id`
                    INNER JOIN `playlists` 
                    ON `playlists`.`id` = `tracks_in_playlists`.`playlistid`
                 	WHERE 
                    `tracks`.`artist` LIKE '%some query%'
                    OR `tracks`.`title` LIKE '%some query%';