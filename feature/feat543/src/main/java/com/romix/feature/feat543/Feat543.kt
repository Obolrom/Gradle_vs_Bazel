package com.romix.feature.feat543

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat543Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat543UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat543FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat543UserSummary
)

data class Feat543UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat543NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat543Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat543Config = Feat543Config()
) {

    fun loadSnapshot(userId: Long): Feat543NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat543NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat543UserSummary {
        return Feat543UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat543FeedItem> {
        val result = java.util.ArrayList<Feat543FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat543FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat543UiMapper {

    fun mapToUi(model: List<Feat543FeedItem>): Feat543UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat543UiModel(
            header = UiText("Feat543 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat543UiModel =
        Feat543UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat543UiModel =
        Feat543UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat543UiModel =
        Feat543UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat543Service(
    private val repository: Feat543Repository,
    private val uiMapper: Feat543UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat543UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat543UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat543UserItem1(val user: CoreUser, val label: String)
data class Feat543UserItem2(val user: CoreUser, val label: String)
data class Feat543UserItem3(val user: CoreUser, val label: String)
data class Feat543UserItem4(val user: CoreUser, val label: String)
data class Feat543UserItem5(val user: CoreUser, val label: String)
data class Feat543UserItem6(val user: CoreUser, val label: String)
data class Feat543UserItem7(val user: CoreUser, val label: String)
data class Feat543UserItem8(val user: CoreUser, val label: String)
data class Feat543UserItem9(val user: CoreUser, val label: String)
data class Feat543UserItem10(val user: CoreUser, val label: String)

data class Feat543StateBlock1(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock2(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock3(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock4(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock5(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock6(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock7(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock8(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock9(val state: Feat543UiModel, val checksum: Int)
data class Feat543StateBlock10(val state: Feat543UiModel, val checksum: Int)

fun buildFeat543UserItem(user: CoreUser, index: Int): Feat543UserItem1 {
    return Feat543UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat543StateBlock(model: Feat543UiModel): Feat543StateBlock1 {
    return Feat543StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat543UserSummary> {
    val list = java.util.ArrayList<Feat543UserSummary>(users.size)
    for (user in users) {
        list += Feat543UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat543UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat543UiModel {
    val summaries = (0 until count).map {
        Feat543UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat543UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat543UiModel> {
    val models = java.util.ArrayList<Feat543UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat543AnalyticsEvent1(val name: String, val value: String)
data class Feat543AnalyticsEvent2(val name: String, val value: String)
data class Feat543AnalyticsEvent3(val name: String, val value: String)
data class Feat543AnalyticsEvent4(val name: String, val value: String)
data class Feat543AnalyticsEvent5(val name: String, val value: String)
data class Feat543AnalyticsEvent6(val name: String, val value: String)
data class Feat543AnalyticsEvent7(val name: String, val value: String)
data class Feat543AnalyticsEvent8(val name: String, val value: String)
data class Feat543AnalyticsEvent9(val name: String, val value: String)
data class Feat543AnalyticsEvent10(val name: String, val value: String)

fun logFeat543Event1(event: Feat543AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat543Event2(event: Feat543AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat543Event3(event: Feat543AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat543Event4(event: Feat543AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat543Event5(event: Feat543AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat543Event6(event: Feat543AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat543Event7(event: Feat543AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat543Event8(event: Feat543AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat543Event9(event: Feat543AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat543Event10(event: Feat543AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat543Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat543Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat543(u: CoreUser): Feat543Projection1 =
    Feat543Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat543Projection1> {
    val list = java.util.ArrayList<Feat543Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat543(u)
    }
    return list
}
