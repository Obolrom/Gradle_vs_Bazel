package com.romix.feature.feat118

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat118Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat118UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat118FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat118UserSummary
)

data class Feat118UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat118NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat118Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat118Config = Feat118Config()
) {

    fun loadSnapshot(userId: Long): Feat118NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat118NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat118UserSummary {
        return Feat118UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat118FeedItem> {
        val result = java.util.ArrayList<Feat118FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat118FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat118UiMapper {

    fun mapToUi(model: List<Feat118FeedItem>): Feat118UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat118UiModel(
            header = UiText("Feat118 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat118UiModel =
        Feat118UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat118UiModel =
        Feat118UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat118UiModel =
        Feat118UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat118Service(
    private val repository: Feat118Repository,
    private val uiMapper: Feat118UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat118UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat118UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat118UserItem1(val user: CoreUser, val label: String)
data class Feat118UserItem2(val user: CoreUser, val label: String)
data class Feat118UserItem3(val user: CoreUser, val label: String)
data class Feat118UserItem4(val user: CoreUser, val label: String)
data class Feat118UserItem5(val user: CoreUser, val label: String)
data class Feat118UserItem6(val user: CoreUser, val label: String)
data class Feat118UserItem7(val user: CoreUser, val label: String)
data class Feat118UserItem8(val user: CoreUser, val label: String)
data class Feat118UserItem9(val user: CoreUser, val label: String)
data class Feat118UserItem10(val user: CoreUser, val label: String)

data class Feat118StateBlock1(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock2(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock3(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock4(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock5(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock6(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock7(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock8(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock9(val state: Feat118UiModel, val checksum: Int)
data class Feat118StateBlock10(val state: Feat118UiModel, val checksum: Int)

fun buildFeat118UserItem(user: CoreUser, index: Int): Feat118UserItem1 {
    return Feat118UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat118StateBlock(model: Feat118UiModel): Feat118StateBlock1 {
    return Feat118StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat118UserSummary> {
    val list = java.util.ArrayList<Feat118UserSummary>(users.size)
    for (user in users) {
        list += Feat118UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat118UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat118UiModel {
    val summaries = (0 until count).map {
        Feat118UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat118UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat118UiModel> {
    val models = java.util.ArrayList<Feat118UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat118AnalyticsEvent1(val name: String, val value: String)
data class Feat118AnalyticsEvent2(val name: String, val value: String)
data class Feat118AnalyticsEvent3(val name: String, val value: String)
data class Feat118AnalyticsEvent4(val name: String, val value: String)
data class Feat118AnalyticsEvent5(val name: String, val value: String)
data class Feat118AnalyticsEvent6(val name: String, val value: String)
data class Feat118AnalyticsEvent7(val name: String, val value: String)
data class Feat118AnalyticsEvent8(val name: String, val value: String)
data class Feat118AnalyticsEvent9(val name: String, val value: String)
data class Feat118AnalyticsEvent10(val name: String, val value: String)

fun logFeat118Event1(event: Feat118AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat118Event2(event: Feat118AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat118Event3(event: Feat118AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat118Event4(event: Feat118AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat118Event5(event: Feat118AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat118Event6(event: Feat118AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat118Event7(event: Feat118AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat118Event8(event: Feat118AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat118Event9(event: Feat118AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat118Event10(event: Feat118AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat118Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat118Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat118(u: CoreUser): Feat118Projection1 =
    Feat118Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat118Projection1> {
    val list = java.util.ArrayList<Feat118Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat118(u)
    }
    return list
}
