package com.romix.feature.feat598

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat598Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat598UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat598FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat598UserSummary
)

data class Feat598UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat598NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat598Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat598Config = Feat598Config()
) {

    fun loadSnapshot(userId: Long): Feat598NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat598NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat598UserSummary {
        return Feat598UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat598FeedItem> {
        val result = java.util.ArrayList<Feat598FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat598FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat598UiMapper {

    fun mapToUi(model: List<Feat598FeedItem>): Feat598UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat598UiModel(
            header = UiText("Feat598 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat598UiModel =
        Feat598UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat598UiModel =
        Feat598UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat598UiModel =
        Feat598UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat598Service(
    private val repository: Feat598Repository,
    private val uiMapper: Feat598UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat598UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat598UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat598UserItem1(val user: CoreUser, val label: String)
data class Feat598UserItem2(val user: CoreUser, val label: String)
data class Feat598UserItem3(val user: CoreUser, val label: String)
data class Feat598UserItem4(val user: CoreUser, val label: String)
data class Feat598UserItem5(val user: CoreUser, val label: String)
data class Feat598UserItem6(val user: CoreUser, val label: String)
data class Feat598UserItem7(val user: CoreUser, val label: String)
data class Feat598UserItem8(val user: CoreUser, val label: String)
data class Feat598UserItem9(val user: CoreUser, val label: String)
data class Feat598UserItem10(val user: CoreUser, val label: String)

data class Feat598StateBlock1(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock2(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock3(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock4(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock5(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock6(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock7(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock8(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock9(val state: Feat598UiModel, val checksum: Int)
data class Feat598StateBlock10(val state: Feat598UiModel, val checksum: Int)

fun buildFeat598UserItem(user: CoreUser, index: Int): Feat598UserItem1 {
    return Feat598UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat598StateBlock(model: Feat598UiModel): Feat598StateBlock1 {
    return Feat598StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat598UserSummary> {
    val list = java.util.ArrayList<Feat598UserSummary>(users.size)
    for (user in users) {
        list += Feat598UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat598UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat598UiModel {
    val summaries = (0 until count).map {
        Feat598UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat598UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat598UiModel> {
    val models = java.util.ArrayList<Feat598UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat598AnalyticsEvent1(val name: String, val value: String)
data class Feat598AnalyticsEvent2(val name: String, val value: String)
data class Feat598AnalyticsEvent3(val name: String, val value: String)
data class Feat598AnalyticsEvent4(val name: String, val value: String)
data class Feat598AnalyticsEvent5(val name: String, val value: String)
data class Feat598AnalyticsEvent6(val name: String, val value: String)
data class Feat598AnalyticsEvent7(val name: String, val value: String)
data class Feat598AnalyticsEvent8(val name: String, val value: String)
data class Feat598AnalyticsEvent9(val name: String, val value: String)
data class Feat598AnalyticsEvent10(val name: String, val value: String)

fun logFeat598Event1(event: Feat598AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat598Event2(event: Feat598AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat598Event3(event: Feat598AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat598Event4(event: Feat598AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat598Event5(event: Feat598AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat598Event6(event: Feat598AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat598Event7(event: Feat598AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat598Event8(event: Feat598AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat598Event9(event: Feat598AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat598Event10(event: Feat598AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat598Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat598Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat598(u: CoreUser): Feat598Projection1 =
    Feat598Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat598Projection1> {
    val list = java.util.ArrayList<Feat598Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat598(u)
    }
    return list
}
