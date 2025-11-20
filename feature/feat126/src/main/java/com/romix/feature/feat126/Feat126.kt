package com.romix.feature.feat126

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat126Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat126UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat126FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat126UserSummary
)

data class Feat126UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat126NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat126Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat126Config = Feat126Config()
) {

    fun loadSnapshot(userId: Long): Feat126NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat126NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat126UserSummary {
        return Feat126UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat126FeedItem> {
        val result = java.util.ArrayList<Feat126FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat126FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat126UiMapper {

    fun mapToUi(model: List<Feat126FeedItem>): Feat126UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat126UiModel(
            header = UiText("Feat126 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat126UiModel =
        Feat126UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat126UiModel =
        Feat126UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat126UiModel =
        Feat126UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat126Service(
    private val repository: Feat126Repository,
    private val uiMapper: Feat126UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat126UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat126UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat126UserItem1(val user: CoreUser, val label: String)
data class Feat126UserItem2(val user: CoreUser, val label: String)
data class Feat126UserItem3(val user: CoreUser, val label: String)
data class Feat126UserItem4(val user: CoreUser, val label: String)
data class Feat126UserItem5(val user: CoreUser, val label: String)
data class Feat126UserItem6(val user: CoreUser, val label: String)
data class Feat126UserItem7(val user: CoreUser, val label: String)
data class Feat126UserItem8(val user: CoreUser, val label: String)
data class Feat126UserItem9(val user: CoreUser, val label: String)
data class Feat126UserItem10(val user: CoreUser, val label: String)

data class Feat126StateBlock1(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock2(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock3(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock4(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock5(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock6(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock7(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock8(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock9(val state: Feat126UiModel, val checksum: Int)
data class Feat126StateBlock10(val state: Feat126UiModel, val checksum: Int)

fun buildFeat126UserItem(user: CoreUser, index: Int): Feat126UserItem1 {
    return Feat126UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat126StateBlock(model: Feat126UiModel): Feat126StateBlock1 {
    return Feat126StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat126UserSummary> {
    val list = java.util.ArrayList<Feat126UserSummary>(users.size)
    for (user in users) {
        list += Feat126UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat126UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat126UiModel {
    val summaries = (0 until count).map {
        Feat126UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat126UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat126UiModel> {
    val models = java.util.ArrayList<Feat126UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat126AnalyticsEvent1(val name: String, val value: String)
data class Feat126AnalyticsEvent2(val name: String, val value: String)
data class Feat126AnalyticsEvent3(val name: String, val value: String)
data class Feat126AnalyticsEvent4(val name: String, val value: String)
data class Feat126AnalyticsEvent5(val name: String, val value: String)
data class Feat126AnalyticsEvent6(val name: String, val value: String)
data class Feat126AnalyticsEvent7(val name: String, val value: String)
data class Feat126AnalyticsEvent8(val name: String, val value: String)
data class Feat126AnalyticsEvent9(val name: String, val value: String)
data class Feat126AnalyticsEvent10(val name: String, val value: String)

fun logFeat126Event1(event: Feat126AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat126Event2(event: Feat126AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat126Event3(event: Feat126AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat126Event4(event: Feat126AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat126Event5(event: Feat126AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat126Event6(event: Feat126AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat126Event7(event: Feat126AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat126Event8(event: Feat126AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat126Event9(event: Feat126AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat126Event10(event: Feat126AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat126Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat126Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat126(u: CoreUser): Feat126Projection1 =
    Feat126Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat126Projection1> {
    val list = java.util.ArrayList<Feat126Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat126(u)
    }
    return list
}
