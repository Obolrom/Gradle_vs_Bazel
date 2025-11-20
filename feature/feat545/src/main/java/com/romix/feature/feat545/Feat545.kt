package com.romix.feature.feat545

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat545Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat545UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat545FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat545UserSummary
)

data class Feat545UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat545NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat545Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat545Config = Feat545Config()
) {

    fun loadSnapshot(userId: Long): Feat545NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat545NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat545UserSummary {
        return Feat545UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat545FeedItem> {
        val result = java.util.ArrayList<Feat545FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat545FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat545UiMapper {

    fun mapToUi(model: List<Feat545FeedItem>): Feat545UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat545UiModel(
            header = UiText("Feat545 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat545UiModel =
        Feat545UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat545UiModel =
        Feat545UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat545UiModel =
        Feat545UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat545Service(
    private val repository: Feat545Repository,
    private val uiMapper: Feat545UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat545UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat545UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat545UserItem1(val user: CoreUser, val label: String)
data class Feat545UserItem2(val user: CoreUser, val label: String)
data class Feat545UserItem3(val user: CoreUser, val label: String)
data class Feat545UserItem4(val user: CoreUser, val label: String)
data class Feat545UserItem5(val user: CoreUser, val label: String)
data class Feat545UserItem6(val user: CoreUser, val label: String)
data class Feat545UserItem7(val user: CoreUser, val label: String)
data class Feat545UserItem8(val user: CoreUser, val label: String)
data class Feat545UserItem9(val user: CoreUser, val label: String)
data class Feat545UserItem10(val user: CoreUser, val label: String)

data class Feat545StateBlock1(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock2(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock3(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock4(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock5(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock6(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock7(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock8(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock9(val state: Feat545UiModel, val checksum: Int)
data class Feat545StateBlock10(val state: Feat545UiModel, val checksum: Int)

fun buildFeat545UserItem(user: CoreUser, index: Int): Feat545UserItem1 {
    return Feat545UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat545StateBlock(model: Feat545UiModel): Feat545StateBlock1 {
    return Feat545StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat545UserSummary> {
    val list = java.util.ArrayList<Feat545UserSummary>(users.size)
    for (user in users) {
        list += Feat545UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat545UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat545UiModel {
    val summaries = (0 until count).map {
        Feat545UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat545UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat545UiModel> {
    val models = java.util.ArrayList<Feat545UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat545AnalyticsEvent1(val name: String, val value: String)
data class Feat545AnalyticsEvent2(val name: String, val value: String)
data class Feat545AnalyticsEvent3(val name: String, val value: String)
data class Feat545AnalyticsEvent4(val name: String, val value: String)
data class Feat545AnalyticsEvent5(val name: String, val value: String)
data class Feat545AnalyticsEvent6(val name: String, val value: String)
data class Feat545AnalyticsEvent7(val name: String, val value: String)
data class Feat545AnalyticsEvent8(val name: String, val value: String)
data class Feat545AnalyticsEvent9(val name: String, val value: String)
data class Feat545AnalyticsEvent10(val name: String, val value: String)

fun logFeat545Event1(event: Feat545AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat545Event2(event: Feat545AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat545Event3(event: Feat545AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat545Event4(event: Feat545AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat545Event5(event: Feat545AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat545Event6(event: Feat545AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat545Event7(event: Feat545AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat545Event8(event: Feat545AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat545Event9(event: Feat545AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat545Event10(event: Feat545AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat545Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat545Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat545(u: CoreUser): Feat545Projection1 =
    Feat545Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat545Projection1> {
    val list = java.util.ArrayList<Feat545Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat545(u)
    }
    return list
}
