package com.romix.feature.feat586

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat586Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat586UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat586FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat586UserSummary
)

data class Feat586UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat586NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat586Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat586Config = Feat586Config()
) {

    fun loadSnapshot(userId: Long): Feat586NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat586NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat586UserSummary {
        return Feat586UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat586FeedItem> {
        val result = java.util.ArrayList<Feat586FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat586FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat586UiMapper {

    fun mapToUi(model: List<Feat586FeedItem>): Feat586UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat586UiModel(
            header = UiText("Feat586 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat586UiModel =
        Feat586UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat586UiModel =
        Feat586UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat586UiModel =
        Feat586UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat586Service(
    private val repository: Feat586Repository,
    private val uiMapper: Feat586UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat586UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat586UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat586UserItem1(val user: CoreUser, val label: String)
data class Feat586UserItem2(val user: CoreUser, val label: String)
data class Feat586UserItem3(val user: CoreUser, val label: String)
data class Feat586UserItem4(val user: CoreUser, val label: String)
data class Feat586UserItem5(val user: CoreUser, val label: String)
data class Feat586UserItem6(val user: CoreUser, val label: String)
data class Feat586UserItem7(val user: CoreUser, val label: String)
data class Feat586UserItem8(val user: CoreUser, val label: String)
data class Feat586UserItem9(val user: CoreUser, val label: String)
data class Feat586UserItem10(val user: CoreUser, val label: String)

data class Feat586StateBlock1(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock2(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock3(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock4(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock5(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock6(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock7(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock8(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock9(val state: Feat586UiModel, val checksum: Int)
data class Feat586StateBlock10(val state: Feat586UiModel, val checksum: Int)

fun buildFeat586UserItem(user: CoreUser, index: Int): Feat586UserItem1 {
    return Feat586UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat586StateBlock(model: Feat586UiModel): Feat586StateBlock1 {
    return Feat586StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat586UserSummary> {
    val list = java.util.ArrayList<Feat586UserSummary>(users.size)
    for (user in users) {
        list += Feat586UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat586UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat586UiModel {
    val summaries = (0 until count).map {
        Feat586UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat586UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat586UiModel> {
    val models = java.util.ArrayList<Feat586UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat586AnalyticsEvent1(val name: String, val value: String)
data class Feat586AnalyticsEvent2(val name: String, val value: String)
data class Feat586AnalyticsEvent3(val name: String, val value: String)
data class Feat586AnalyticsEvent4(val name: String, val value: String)
data class Feat586AnalyticsEvent5(val name: String, val value: String)
data class Feat586AnalyticsEvent6(val name: String, val value: String)
data class Feat586AnalyticsEvent7(val name: String, val value: String)
data class Feat586AnalyticsEvent8(val name: String, val value: String)
data class Feat586AnalyticsEvent9(val name: String, val value: String)
data class Feat586AnalyticsEvent10(val name: String, val value: String)

fun logFeat586Event1(event: Feat586AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat586Event2(event: Feat586AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat586Event3(event: Feat586AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat586Event4(event: Feat586AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat586Event5(event: Feat586AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat586Event6(event: Feat586AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat586Event7(event: Feat586AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat586Event8(event: Feat586AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat586Event9(event: Feat586AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat586Event10(event: Feat586AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat586Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat586Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat586(u: CoreUser): Feat586Projection1 =
    Feat586Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat586Projection1> {
    val list = java.util.ArrayList<Feat586Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat586(u)
    }
    return list
}
