package com.romix.feature.feat301

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat301Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat301UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat301FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat301UserSummary
)

data class Feat301UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat301NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat301Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat301Config = Feat301Config()
) {

    fun loadSnapshot(userId: Long): Feat301NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat301NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat301UserSummary {
        return Feat301UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat301FeedItem> {
        val result = java.util.ArrayList<Feat301FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat301FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat301UiMapper {

    fun mapToUi(model: List<Feat301FeedItem>): Feat301UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat301UiModel(
            header = UiText("Feat301 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat301UiModel =
        Feat301UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat301UiModel =
        Feat301UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat301UiModel =
        Feat301UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat301Service(
    private val repository: Feat301Repository,
    private val uiMapper: Feat301UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat301UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat301UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat301UserItem1(val user: CoreUser, val label: String)
data class Feat301UserItem2(val user: CoreUser, val label: String)
data class Feat301UserItem3(val user: CoreUser, val label: String)
data class Feat301UserItem4(val user: CoreUser, val label: String)
data class Feat301UserItem5(val user: CoreUser, val label: String)
data class Feat301UserItem6(val user: CoreUser, val label: String)
data class Feat301UserItem7(val user: CoreUser, val label: String)
data class Feat301UserItem8(val user: CoreUser, val label: String)
data class Feat301UserItem9(val user: CoreUser, val label: String)
data class Feat301UserItem10(val user: CoreUser, val label: String)

data class Feat301StateBlock1(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock2(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock3(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock4(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock5(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock6(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock7(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock8(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock9(val state: Feat301UiModel, val checksum: Int)
data class Feat301StateBlock10(val state: Feat301UiModel, val checksum: Int)

fun buildFeat301UserItem(user: CoreUser, index: Int): Feat301UserItem1 {
    return Feat301UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat301StateBlock(model: Feat301UiModel): Feat301StateBlock1 {
    return Feat301StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat301UserSummary> {
    val list = java.util.ArrayList<Feat301UserSummary>(users.size)
    for (user in users) {
        list += Feat301UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat301UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat301UiModel {
    val summaries = (0 until count).map {
        Feat301UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat301UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat301UiModel> {
    val models = java.util.ArrayList<Feat301UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat301AnalyticsEvent1(val name: String, val value: String)
data class Feat301AnalyticsEvent2(val name: String, val value: String)
data class Feat301AnalyticsEvent3(val name: String, val value: String)
data class Feat301AnalyticsEvent4(val name: String, val value: String)
data class Feat301AnalyticsEvent5(val name: String, val value: String)
data class Feat301AnalyticsEvent6(val name: String, val value: String)
data class Feat301AnalyticsEvent7(val name: String, val value: String)
data class Feat301AnalyticsEvent8(val name: String, val value: String)
data class Feat301AnalyticsEvent9(val name: String, val value: String)
data class Feat301AnalyticsEvent10(val name: String, val value: String)

fun logFeat301Event1(event: Feat301AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat301Event2(event: Feat301AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat301Event3(event: Feat301AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat301Event4(event: Feat301AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat301Event5(event: Feat301AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat301Event6(event: Feat301AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat301Event7(event: Feat301AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat301Event8(event: Feat301AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat301Event9(event: Feat301AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat301Event10(event: Feat301AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat301Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat301Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat301(u: CoreUser): Feat301Projection1 =
    Feat301Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat301Projection1> {
    val list = java.util.ArrayList<Feat301Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat301(u)
    }
    return list
}
