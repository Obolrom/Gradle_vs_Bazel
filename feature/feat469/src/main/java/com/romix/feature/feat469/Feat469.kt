package com.romix.feature.feat469

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat469Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat469UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat469FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat469UserSummary
)

data class Feat469UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat469NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat469Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat469Config = Feat469Config()
) {

    fun loadSnapshot(userId: Long): Feat469NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat469NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat469UserSummary {
        return Feat469UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat469FeedItem> {
        val result = java.util.ArrayList<Feat469FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat469FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat469UiMapper {

    fun mapToUi(model: List<Feat469FeedItem>): Feat469UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat469UiModel(
            header = UiText("Feat469 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat469UiModel =
        Feat469UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat469UiModel =
        Feat469UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat469UiModel =
        Feat469UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat469Service(
    private val repository: Feat469Repository,
    private val uiMapper: Feat469UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat469UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat469UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat469UserItem1(val user: CoreUser, val label: String)
data class Feat469UserItem2(val user: CoreUser, val label: String)
data class Feat469UserItem3(val user: CoreUser, val label: String)
data class Feat469UserItem4(val user: CoreUser, val label: String)
data class Feat469UserItem5(val user: CoreUser, val label: String)
data class Feat469UserItem6(val user: CoreUser, val label: String)
data class Feat469UserItem7(val user: CoreUser, val label: String)
data class Feat469UserItem8(val user: CoreUser, val label: String)
data class Feat469UserItem9(val user: CoreUser, val label: String)
data class Feat469UserItem10(val user: CoreUser, val label: String)

data class Feat469StateBlock1(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock2(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock3(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock4(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock5(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock6(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock7(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock8(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock9(val state: Feat469UiModel, val checksum: Int)
data class Feat469StateBlock10(val state: Feat469UiModel, val checksum: Int)

fun buildFeat469UserItem(user: CoreUser, index: Int): Feat469UserItem1 {
    return Feat469UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat469StateBlock(model: Feat469UiModel): Feat469StateBlock1 {
    return Feat469StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat469UserSummary> {
    val list = java.util.ArrayList<Feat469UserSummary>(users.size)
    for (user in users) {
        list += Feat469UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat469UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat469UiModel {
    val summaries = (0 until count).map {
        Feat469UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat469UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat469UiModel> {
    val models = java.util.ArrayList<Feat469UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat469AnalyticsEvent1(val name: String, val value: String)
data class Feat469AnalyticsEvent2(val name: String, val value: String)
data class Feat469AnalyticsEvent3(val name: String, val value: String)
data class Feat469AnalyticsEvent4(val name: String, val value: String)
data class Feat469AnalyticsEvent5(val name: String, val value: String)
data class Feat469AnalyticsEvent6(val name: String, val value: String)
data class Feat469AnalyticsEvent7(val name: String, val value: String)
data class Feat469AnalyticsEvent8(val name: String, val value: String)
data class Feat469AnalyticsEvent9(val name: String, val value: String)
data class Feat469AnalyticsEvent10(val name: String, val value: String)

fun logFeat469Event1(event: Feat469AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat469Event2(event: Feat469AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat469Event3(event: Feat469AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat469Event4(event: Feat469AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat469Event5(event: Feat469AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat469Event6(event: Feat469AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat469Event7(event: Feat469AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat469Event8(event: Feat469AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat469Event9(event: Feat469AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat469Event10(event: Feat469AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat469Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat469Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat469(u: CoreUser): Feat469Projection1 =
    Feat469Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat469Projection1> {
    val list = java.util.ArrayList<Feat469Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat469(u)
    }
    return list
}
