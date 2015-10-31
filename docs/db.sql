--
-- 数据库: `test`
--

-- --------------------------------------------------------

--
-- 表的结构 `admin_info`
--

CREATE TABLE IF NOT EXISTS `admin_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='管理员账号' AUTO_INCREMENT=3 ;

--
-- 转存表中的数据 `admin_info`
--

INSERT INTO `admin_info` (`id`, `display_name`) VALUES
(1, 'simple'),
(2, 'test');
